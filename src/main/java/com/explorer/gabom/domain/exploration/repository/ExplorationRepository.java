package com.explorer.gabom.domain.exploration.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.exploration.entity.Exploration;

public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

	boolean existsByUserIdAndPlaceIdAndEndAtAfter(Long userId, Long placeId, LocalDateTime now);

	Optional<Exploration> findTopByUserIdAndEndAtAfterOrderByEndAtAsc(Long userId, LocalDateTime now);

	// 앱 부팅 시 복구용: 아직 진행 중이고 endAt이 미래인 탐험들
	List<Exploration> findAllByStatusAndEndAtAfter(Exploration.Status status, LocalDateTime now);
	List<Exploration> findAllByUserIdAndEndAtAfterOrderByEndAtAsc(Long userId, LocalDateTime now);
}
