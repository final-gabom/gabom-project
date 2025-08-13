package com.explorer.gabom.domain.ranking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.ranking.entity.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

	Optional<Ranking> findByUser_Id(Long userId);

	List<Ranking> findAllByOrderByExpDescIdAsc();

	@EntityGraph(attributePaths = {"user", "user.title", "user.profileImg"})
	Page<Ranking> findAllByOrderByExpDescIdAsc(Pageable pageable);
}
