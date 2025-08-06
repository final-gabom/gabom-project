package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;

public interface MissionProofQueryRepository {
	List<MissionProofSummary> searchByCondition(MissionProofSearchCondition condition);
	long countByCondition(MissionProofSearchCondition condition);
}