package com.explorer.gabom.domain.Exploration.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.Exploration.entity.Exploration;

public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

	boolean existsByUserIdAndPlaceIdAndEndAtAfter(Long userId, Long placeId, LocalDateTime now);
}
