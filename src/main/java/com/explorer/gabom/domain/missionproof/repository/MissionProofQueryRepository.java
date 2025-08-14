package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;

public interface MissionProofQueryRepository {
	Page<MissionProof> searchByCondition(MissionProofSearchCondition condition, Pageable pageable);
	long countByCondition(MissionProofSearchCondition condition);
}