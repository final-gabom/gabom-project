package com.explorer.gabom.domain.missionproof.service;



import static com.explorer.gabom.global.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;

import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MissionProofServiceImpl implements MissionProofService{

	private final MissionProofRepository missionProofRepository;
	private final PlaceRepository placeRepository;
	private final AttachmentFileRepository attachmentFileRepository;


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
			.findAllByFilePathIn(request.getImgFiles());

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

		// 6. 응답 생성
		return CreateMissionProofResponse.builder()
										 .id(existing.getId())
										 .fieldType(existing.getFieldType())
										 .writer(writer)
										 .title(existing.getTitle())
										 .content(existing.getContent())
										 .createdAt(existing.getCreatedAt())
										 .updatedAt(existing.getUpdatedAt())
										 .profileImages(profileImagePath)
										 .build();
	}
}
