package com.explorer.gabom.domain.missionproof.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MissionProofServiceImpl implements MissionProofService{

	private final MissionProofRepository missionProofRepository;
	private final UserRepository userRepository;
	private final PlaceRepository placeRepository; // PLACE 인증인 경우 사용


	@Override
	@Transactional
	public CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request,
														 CustomUserDetails userDetails) {
		User user = userRepository.findById(userDetails.getUserId())
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 2. fieldType = PLACE일 경우 장소 유효성 검증 및 엔티티 조회
		Place place = null;
		if ("PLACE".equalsIgnoreCase(request.getFieldType())) {
			place = placeRepository.findById(request.getTargetId())
								   .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		} else {
			throw new CustomException(ErrorCode.INVALID_PROOF_TYPE);
		}

		// 3. 엔티티 생성 및 저장
		// 3. 인증 엔티티 생성 및 저장
		MissionProof proof = MissionProof.builder()
										 .user(user)
										 .place(place)  // 🔥 여기가 중요!
										 .fieldType(request.getFieldType().toUpperCase())
										 .targetId(request.getTargetId())
										 .title(request.getTitle())
										 .content(request.getContent())
										 .imageUrls(request.getImageId() != null ? request.getImageId() : new ArrayList<>())
										 .starRating(request.getStarRating())
										 .build();

		MissionProof saved = missionProofRepository.save(proof);

		// 4. 응답 반환
		return CreateMissionProofResponse.builder()
										 .missionProofId(saved.getId())
										 .fieldType(saved.getFieldType())
										 .writerId(user.getId())
										 .writerNickname(user.getNickname())
										 .writerLevel(user.getLevel())
										 .writerTitle(user.getTitle() != null ? user.getTitle().getName() : null)
										 .writerProfileImageUrl(user.getProfileImg() != null ? user.getProfileImg().getFileUrl() : null)
										 .title(saved.getTitle())
										 .content(saved.getContent())
										 .createdAt(saved.getCreatedAt())
										 .updatedAt(saved.getUpdatedAt())
										 .build();

	}
}