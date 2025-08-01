package com.explorer.gabom.domain.missionproof.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;

public interface MissionProofRepositoryCustom {
	Page<MissionProof> searchMissionProofs(ListMissionProofRequest request, Pageable pageable);
}
