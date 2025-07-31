package com.explorer.gabom.domain.missionproof.service;



import static com.explorer.gabom.global.exception.ErrorCode.*;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MissionProofServiceImpl implements MissionProofService{

	private final MissionProofRepository missionProofRepository;
	private final PlaceRepository placeRepository;
	private final AttachmentFileRepository attachmentFileRepository;


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

	@Override
	@Transactional
	public void deleteMissionProof(Long id, Long userId) {
		MissionProof missionProof = missionProofRepository.findById(id)
														  .orElseThrow(() -> new CustomException(NOT_FOUND_MISSION_PROOF));

		if (!missionProof.getUser().getId().equals(userId)) {
			throw new CustomException(FORBIDDEN_DELETE_MISSION_PROOF);
		}

		missionProof.delete();
	}
}
