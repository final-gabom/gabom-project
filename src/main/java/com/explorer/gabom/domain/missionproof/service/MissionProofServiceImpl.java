package com.explorer.gabom.domain.missionproof.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.notification.event.MissionProofCreatedEvent;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.validator.AuthorValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionProofServiceImpl implements MissionProofService {

	private final MissionProofRepository missionProofRepository;
	private final PlaceRepository placeRepository;
	private final AttachmentFileRepository attachmentFileRepository;
	private final AuthorValidator authorValidator;
	private final NotificationService notificationService;
	private final ApplicationEventPublisher eventPublisher;

	// 생성
	@Override
	@Transactional
	public CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request, User loginUser) {

		// PLACE 타입이면 Place 유효성 검증
		Place place = null;
		if (request.getFieldType() == MissionProofType.PLACE) {
			place = placeRepository.findById(request.getTargetId())
								   .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		}

		// 이미지 파일 조회
		List<String> imageIds = request.getData().getImageId();
		List<AttachmentFile> imageFiles = (imageIds != null && !imageIds.isEmpty())
										  ? attachmentFileRepository.findAllById(imageIds)
										  : new ArrayList<>();

		// 엔티티 생성
		MissionProof missionProof = MissionProof.builder()
												.user(loginUser)
												.place(place)
												.targetId(request.getTargetId())
												.fieldType(request.getFieldType())
												.title(request.getData().getTitle())
												.content(request.getData().getContent())
												.starRating(request.getData().getStarRating())
												.imageFiles(imageFiles)
												.build();

		// 저장
		MissionProof savedMissionProof = missionProofRepository.save(missionProof);

		// 알림: 장소 주인에게 “인증글 등록됨”
		if (place != null && place.getUser() != null) {
			Long receiverId = place.getUser().getId();
			String msg  = "내 장소에 인증글이 등록되었습니다.";
			String link = "/mission-proofs/" + savedMissionProof.getId();

			var event = new MissionProofCreatedEvent(
				receiverId,
				"내 장소에 인증글이 등록되었습니다.",
				link,
				NotificationRefType.AUTH_POST,
				savedMissionProof.getId()
			);
			eventPublisher.publishEvent(event);
			log.info("[AUTH_POST] published event AFTER_COMMIT → receiver={}, refId={}", receiverId, savedMissionProof.getId());


			notificationService.notify(
				receiverId,
				NotificationType.AUTH_POST_CREATED,   // 프로젝트 enum 이름에 맞게
				msg,
				link,
				NotificationRefType.AUTH_POST,
				savedMissionProof.getId()
			);
		}

		return CreateMissionProofResponse.toDto(savedMissionProof);
	}

	// 미션 인증글 수정
	@Override
	@Transactional
	public CreateMissionProofResponse updateMissionProof(Long id, UpdateMissionProofRequest request, Long userId) {
		// 1. 기존 인증글 조회
		MissionProof existing = missionProofRepository.findById(id)
													  .orElseThrow(() -> new CustomException(NOT_FOUND_MISSION_PROOF));

		// 2. 작성자 검증
		if (!existing.getUser().getId().equals(userId)) {
			throw new CustomException(FORBIDDEN_UPDATE_MISSION_PROOF);
		}

		// 3. 이미지 파일 연관 조회
		List<AttachmentFile> imageFiles = attachmentFileRepository
			.findAllByFileIdIn(request.getImgFileIds());

		// 4. 엔티티 수정
		existing.update(request.getTitle(), request.getContent(), imageFiles);

		String profileImagePath = null;
		if (!imageFiles.isEmpty()) {
			profileImagePath = imageFiles.get(0).getFilePath();
		}

		// 5. 응답용 UserSummaryDto 생성
		User user = existing.getUser();
		UserSummaryDto writer = UserSummaryDto.builder()
											  .id(user.getId())
											  .nickname(user.getNickname())
											  .level(user.getLevel())
											  .title(user.getTitle() != null ? user.getTitle().getName() : null)
											  .build();

		List<FileResponseDto> profileImages = imageFiles.stream()
														.map(FileResponseDto::toDto)
														.collect(Collectors.toList());

		// 6. 응답 생성
		return CreateMissionProofResponse.builder()
										 .id(existing.getId())
										 .fieldType(existing.getFieldType())
										 .writer(writer)
										 .title(existing.getTitle())
										 .content(existing.getContent())
										 .createdAt(existing.getCreatedAt())
										 .updatedAt(existing.getUpdatedAt())
										 .profileImages(profileImages)
										 .build();
	}

	@Override
	@Transactional
	public void deleteMissionProof(Long id, Long userId) {
		MissionProof missionProof = missionProofRepository.findByIdAndDeletedAtIsNull(id)
														  .orElseThrow(
															  () -> new CustomException(NOT_FOUND_MISSION_PROOF));
		if (!missionProof.getUser().getId().equals(userId)) {
			throw new CustomException(FORBIDDEN_DELETE_MISSION_PROOF);
		}

		missionProof.delete();
	}

	@Override
	@Transactional(readOnly = true)
	public MissionProofDetailResponse getMissionProofDetail(Long id) {
		MissionProof missionProof = missionProofRepository.findByIdAndDeletedAtIsNull(id)
														  .orElseThrow(() -> new CustomException(
															  ErrorCode.NOT_FOUND_MISSION_PROOF));

		List<AttachmentFile> imageFiles = attachmentFileRepository.findAllByRefIdAndFileType(id,
																							 FileType.MISSION_PROOF);

		List<FileResponseDto> profileImages = imageFiles.stream()
														.map(FileResponseDto::toDto)
														.collect(Collectors.toList());

		return MissionProofDetailResponse.toDto(missionProof, profileImages);
	}

	@Transactional(readOnly = true)
	public PageResponse<MissionProofSummary> getMissionProofs(MissionProofSearchCondition condition,
															  Pageable pageable) {
		// Repository에서 페이지 기반 조회
		Page<MissionProof> results = missionProofRepository.searchByCondition(condition, pageable);

		// DTO로 변환
		List<MissionProofSummary> summaries = results.getContent().stream()
													 .map(mp -> new MissionProofSummary(
														 mp.getId(),
														 mp.getFieldType(),
														 new UserSummaryDto(
															 mp.getUser().getId(),
															 mp.getUser().getNickname(),
															 mp.getUser().getLevel(),
															 mp.getUser().getTitle() != null ? mp.getUser()
																								 .getTitle()
																								 .getName() : null
														 ),
														 mp.getTitle(),
														 mp.getCreatedAt(),
														 mp.getUpdatedAt(),
														 mp.getImageFiles().stream()
														   .map(AttachmentFile::getFilePath)
														   .filter(Objects::nonNull)
														   .toList()
													 ))
													 .toList();

		// PageResponse로 변환 후 반환
		return PageResponse.toDto(new PageImpl<>(summaries, pageable, results.getTotalElements()));
	}
}