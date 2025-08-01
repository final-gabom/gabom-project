package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;

public interface MissionProofRepositoryCustom {
	List<MissionProof> searchMissionProofs(ListMissionProofRequest request, int size);
}
