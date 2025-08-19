package com.explorer.gabom.domain.ranking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.explorer.gabom.domain.ranking.entity.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

	Optional<Ranking> findByUser_Id(Long userId);

	@EntityGraph(attributePaths = {"user", "user.title", "user.profileImg"})
	List<Ranking> findAllByUser_IdIn(List<Long> userIds);

	@Query("SELECT r.user.id FROM Ranking r WHERE r.user.nickname LIKE CONCAT(:nickname, '%')")
	List<Long> findUserIdsByNicknameContaining(String nickname);
}
