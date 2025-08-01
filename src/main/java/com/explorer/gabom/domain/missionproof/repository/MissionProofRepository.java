package com.explorer.gabom.domain.missionproof.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.missionproof.entity.MissionProof;

public interface MissionProofRepository extends JpaRepository<MissionProof, Long>, MissionProofRepositoryCustom {
	Optional<MissionProof> findByIdAndDeletedAtIsNull(Long id);

}
