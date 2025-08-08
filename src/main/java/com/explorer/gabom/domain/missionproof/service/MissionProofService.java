package com.explorer.gabom.domain.missionproof.service;

import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.CursorResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.user.entity.User;

public interface MissionProofService {
	CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request, User loginUser);

	CreateMissionProofResponse updateMissionProof(Long id, UpdateMissionProofRequest request, Long userId);

	MissionProofDetailResponse getMissionProofDetail(Long id);

	void deleteMissionProof(Long id, Long userId);

	CursorResponse<MissionProofSummary> getMissionProofs(
		MissionProofSearchCondition condition
	);

}
