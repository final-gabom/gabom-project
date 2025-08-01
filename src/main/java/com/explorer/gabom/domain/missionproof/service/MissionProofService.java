package com.explorer.gabom.domain.missionproof.service;

import com.explorer.gabom.domain.missionproof.dto.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.OffsetResponse;

public interface MissionProofService {
	CreateMissionProofResponse createMissionProof(CreateMissionProofRequest request, User loginUser);

	CreateMissionProofResponse updateMissionProof(Long id, UpdateMissionProofRequest request, Long userId);

	MissionProofDetailResponse getMissionProofDetail(Long id);

	void deleteMissionProof(Long id, Long userId);

	OffsetResponse<MissionProofSummary> getMissionProofs(ListMissionProofRequest request);

}
