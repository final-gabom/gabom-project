package com.explorer.gabom.domain.ranking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.ranking.entity.Ranking;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
	Optional<Ranking> findByUserId(Long userId);

	List<Ranking> findByUserIdIn(List<Long> userIds);
}
