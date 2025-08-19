package com.explorer.gabom.domain.ranking.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.domain.ranking.dto.RankingSummaryDto;
import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

	@InjectMocks
	private RankingServiceImpl rankingService;

	@Mock
	private RankingRepository rankingRepository;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ZSetOperations<String, String> zSetOperations;

	private User user1;
	private User user2;
	private Ranking ranking1;
	private Ranking ranking2;

	@BeforeEach
	void setupUsers() {
		user1 = User.builder()
					.id(1L)
					.email("user1@example.com")
					.password("pw")
					.nickname("user1")
					.userRole(UserRole.USER)
					.build();
		user1.updateLevel(5);
		user1.addExp(100L);
		user1.addPoint(50L);

		user2 = User.builder()
					.id(2L)
					.email("user2@example.com")
					.password("pw")
					.nickname("user2")
					.userRole(UserRole.USER)
					.build();
		user2.updateLevel(10);
		user2.addExp(200L);

		ranking1 = new Ranking(user1, 100L);
		ranking2 = new Ranking(user2, 200L);
	}

	@DisplayName("getRankingPage - nickname 없을 때, Redis와 DB에서 정상 조회")
	@Test
	void getRankingPage_withoutNickname_success() {
		Pageable pageable = PageRequest.of(0, 2);

		Set<String> userIds = new LinkedHashSet<>(Arrays.asList("2", "1"));
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRange("ranking:exp", 0, 1)).willReturn(userIds);
		given(rankingRepository.findAllByUser_IdIn(Arrays.asList(2L, 1L)))
			.willReturn(Arrays.asList(ranking2, ranking1));
		given(zSetOperations.reverseRank("ranking:exp", "2")).willReturn(0L);
		given(zSetOperations.reverseRank("ranking:exp", "1")).willReturn(1L);
		given(zSetOperations.size("ranking:exp")).willReturn(2L);

		PageResponse<RankingSummaryDto> response = rankingService.getRankingPage(pageable, null);

		assertThat(response.getContent()).hasSize(2);
		assertThat(response.getContent().get(0).getUserId()).isEqualTo(2L);
		assertThat(response.getContent().get(0).getRankNo()).isEqualTo(1L);
		assertThat(response.getContent().get(1).getUserId()).isEqualTo(1L);
		assertThat(response.getContent().get(1).getRankNo()).isEqualTo(2L);
		assertThat(response.getTotalElements()).isEqualTo(2L);
	}

	@DisplayName("getRankingPage - nickname 있을 때, Redis rank와 DB 조회")
	@Test
	void getRankingPage_withNickname_success() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Long> candidateIds = Arrays.asList(1L, 2L);

		given(rankingRepository.findUserIdsByNicknameContaining("user")).willReturn(candidateIds);
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRank("ranking:exp", "1")).willReturn(1L);
		given(zSetOperations.reverseRank("ranking:exp", "2")).willReturn(0L);
		given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.of(ranking1));
		given(rankingRepository.findByUser_Id(2L)).willReturn(Optional.of(ranking2));

		PageResponse<RankingSummaryDto> response = rankingService.getRankingPage(pageable, "user");

		assertThat(response.getContent()).hasSize(2);
		assertThat(response.getContent().get(0).getUserId()).isEqualTo(2L);
		assertThat(response.getContent().get(0).getRankNo()).isEqualTo(1L);
		assertThat(response.getContent().get(1).getUserId()).isEqualTo(1L);
		assertThat(response.getContent().get(1).getRankNo()).isEqualTo(2L);
		assertThat(response.getTotalElements()).isEqualTo(2L);
	}

	@DisplayName("getRankingPage - nickname 있을 때, Redis에 rank 없으면 예외")
	@Test
	void getRankingPage_withNickname_redisMissingRank() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Long> candidateIds = Collections.singletonList(1L);

		given(rankingRepository.findUserIdsByNicknameContaining("user")).willReturn(candidateIds);
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRank("ranking:exp", "1")).willReturn(null);

		CustomException ex = assertThrows(CustomException.class,
										  () -> rankingService.getRankingPage(pageable, "user"));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RANKING_NOT_FOUND);
	}

	@DisplayName("getRanking - 정상 조회")
	@Test
	void getRanking_success() {
		given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.of(ranking1));
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRank("ranking:exp", "1")).willReturn(0L);

		RankingDto dto = rankingService.getRanking(1L);

		assertThat(dto.getUserId()).isEqualTo(1L);
		assertThat(dto.getRankNo()).isEqualTo(1L);
		assertThat(dto.getExp()).isEqualTo(100L);
	}

	@DisplayName("getRanking - Redis에 rank 없으면 예외")
	@Test
	void getRanking_redisMissingRank() {
		given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.of(ranking1));
		given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
		given(zSetOperations.reverseRank("ranking:exp", "1")).willReturn(null);

		CustomException ex = assertThrows(CustomException.class, () -> rankingService.getRanking(1L));
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RANKING_NOT_FOUND);
	}

	@DisplayName("getRanking - DB에 ranking 없으면 예외")
	@Test
	void getRanking_dbMissingRanking() {
		given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.empty());

		CustomException ex = assertThrows(CustomException.class, () -> rankingService.getRanking(1L));
		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RANKING_NOT_FOUND);
	}
}
