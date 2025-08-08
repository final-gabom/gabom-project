package com.explorer.gabom.domain.missionproof.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.explorer.gabom.domain.missionproof.dto.response.OffsetResponse;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofQueryRepository;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.validator.AuthorValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MissionProofServiceImpl implements MissionProofService {

	private final MissionProofRepository missionProofRepository;
	private final PlaceRepository placeRepository;
	private final AttachmentFileRepository attachmentFileRepository;
	private final AuthorValidator authorValidator;
	private final MissionProofQueryRepository missionProofQueryRepository;

	// 생성
	@Override
	@Transactional
	public CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request, User loginUser) {

		// 2. PLACE 타입인 경우 Place 유효성 검증
		Place place = null;
		if (request.getFieldType() == MissionProofType.PLACE) {
			place = placeRepository.findById(request.getTargetId())
								   .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		}

		// 3. 이미지 파일 조회
		List<String> imageIds = request.getData().getImageId();
		List<AttachmentFile> imageFiles = imageIds != null && !imageIds.isEmpty()
										  ? attachmentFileRepository.findAllById(imageIds)
										  : new ArrayList<>();

		// 4. MissionProof 엔티티 생성
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

		// 5. 저장
		MissionProof savedMissionProof = missionProofRepository.save(missionProof);

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

	@Override
	public OffsetResponse<MissionProofSummary> getMissionProofs(MissionProofSearchCondition condition) {

		// 검색 조건에 해당하는 미션 인증 요약 리스트 조회
		List<MissionProofSummary> results = missionProofQueryRepository.searchByCondition(condition);

		// 총 데이터 개수 조회 (페이징용 메타데이터)
		long totalElements = missionProofQueryRepository.countByCondition(condition);

		// 결과 리스트를 한 번 더 가공하여 새로운 MissionProofSummary 객체로 매핑
		// (필요 시 내부 객체나 필드에 대해 null 처리 등 후처리를 할 수 있음)
		List<MissionProofSummary> summaries = results.stream()
													 .map(mp -> new MissionProofSummary(
														 mp.getId(),                   // 인증 ID
														 mp.getFieldType(),           // 필드 타입
														 new UserSummaryDto(          // 작성자 정보 재구성
																					  mp.getWriter().getId(),
																					  mp.getWriter().getNickname(),
																					  mp.getWriter().getLevel(),

																					  // title이 null인 경우 작성자의 title도 null로 처리
																					  mp.getTitle() != null
																					  ? mp.getWriter().getTitle()
																					  : null
														 ),
														 mp.getTitle(),               // 인증 제목
														 mp.getCreatedAt(),           // 생성일
														 mp.getUpdatedAt(),           // 수정일
														 mp.getProfileImages()        // 이미지 목록
													 ))
													 .collect(Collectors.toList()); // 최종 리스트 생성

		// 커서 페이징용 lastId 추출 (가장 마지막 ID, 다음 페이지 조회에 사용)
		Long lastId = !results.isEmpty() ? results.get(results.size() - 1).getId() : null;

		// OffsetResponse 객체로 응답 (데이터, 개수, 마지막 ID, 전체 개수 포함)
		return new OffsetResponse<>(summaries, summaries.size(), lastId, totalElements);
	}

}
