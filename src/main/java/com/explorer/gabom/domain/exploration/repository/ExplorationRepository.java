package com.explorer.gabom.domain.exploration.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.exploration.entity.Exploration;

public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

	boolean existsByUserIdAndPlaceIdAndEndAtAfter(Long userId, Long placeId, LocalDateTime now);

	List<Exploration> findAllByUserIdAndEndAtAfterOrderByEndAtAsc(Long userId, LocalDateTime now);
}
