package com.explorer.gabom.domain.ranking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.ranking.entity.Ranking;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
}
