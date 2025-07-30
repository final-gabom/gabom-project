package com.explorer.gabom.domain.missionproof.service;

import static com.explorer.gabom.domain.title.entity.QUserTitle.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MissionProofServiceImpl implements MissionProofService{

	private final MissionProofRepository missionProofRepository;
	private final UserRepository userRepository;
	private final PlaceRepository placeRepository;
	private final AttachmentFileRepository attachmentFileRepository; // PLACE 인증인 경우 사용


	@Override
	@Transactional
	public CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request, CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();

		// 1. 사용자 조회
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
												.user(user)
												.place(place)
												.targetId(request.getTargetId())
												.fieldType(request.getFieldType())
												.title(request.getData().getTitle())
												.content(request.getData().getContent())
												.starRating(request.getData().getStarRating())
												.imageFiles(imageFiles)
												.build();

		// 5. 저장
		missionProofRepository.save(missionProof);

		UserSummaryDto writer = UserSummaryDto.builder()
											  .id(user.getId())
											  .nickname(user.getNickname())
											  .level(user.getLevel())
											  .title(user.getTitle() != null ? user.getTitle().getName() : null)
											  .build();

		// 6. 응답 반환
		return CreateMissionProofResponse.builder()
										 .missionProofId(missionProof.getId())
										 .fieldType(missionProof.getFieldType())
										 .writer(writer)
										 .title(missionProof.getTitle())
										 .content(missionProof.getContent())
										 .createdAt(missionProof.getCreatedAt())
										 .updatedAt(missionProof.getUpdatedAt())
										 .build();
	}
}
