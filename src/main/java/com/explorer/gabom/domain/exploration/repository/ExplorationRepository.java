package com.explorer.gabom.domain.exploration.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.exploration.entity.Exploration;

public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

	boolean existsByUserIdAndPlaceIdAndEndAtAfter(Long userId, Long placeId, LocalDateTime now);

	Optional<Exploration> findTopByUserIdAndEndAtAfterOrderByStartedAtDesc(Long userId, LocalDateTime now);
}
