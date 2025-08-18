package com.explorer.gabom.domain.ranking.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.domain.ranking.dto.RankingSummaryDto;
import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final RankingRepository rankingRepository;
	private final StringRedisTemplate redisTemplate;

	private static final String RANKING_KEY = "ranking:exp";

	@Override
	public PageResponse<RankingSummaryDto> getRankingPage(Pageable pageable, String nickname) {

		// 닉네임이 없으면 Redis에서 페이징된 userId를 가져오고 DB 조회
		if (nickname == null || nickname.isBlank()) {
			long start = pageable.getOffset();
			long end = start + pageable.getPageSize() - 1;

			Set<String> userIds = redisTemplate.opsForZSet()
											   .reverseRange(RANKING_KEY, start, end);

			if (userIds == null || userIds.isEmpty()) {
				return PageResponse.toDto(new PageImpl<>(Collections.emptyList(), pageable, 0));
			}

			List<Ranking> rankings = rankingRepository.findAllByUser_IdIn(
				userIds.stream().map(Long::valueOf).toList()
			);

			List<RankingSummaryDto> dtoList = rankings.stream()
													  .map(r -> {
														  Long rankNo = redisTemplate.opsForZSet()
																					 .reverseRank(RANKING_KEY,
																								  String.valueOf(
																									  r.getUser()
																									   .getId()));
														  if (rankNo == null) {
															  throw new CustomException(ErrorCode.RANKING_NOT_FOUND);
														  }
														  return RankingSummaryDto.toDto(r, rankNo + 1);
													  })
													  .sorted(Comparator.comparingLong(
														  RankingSummaryDto::getRankNo)) // Redis 기준 정렬
													  .toList();

			Long totalCount = redisTemplate.opsForZSet().size(RANKING_KEY);

			return PageResponse.toDto(
				new PageImpl<>(dtoList, pageable, totalCount)
			);
		}

		// 닉네임이 있는 경우: DB에서 후보 userId 추출
		List<Long> candidateIds = rankingRepository.findUserIdsByNicknameContaining(nickname);

		if (candidateIds.isEmpty()) {
			return PageResponse.toDto(new PageImpl<>(Collections.emptyList(), pageable, 0));
		}

		// 후보 userId에 대해 Redis rank 가져오기
		List<RankingSummaryDto> dtoList = candidateIds.stream()
													  .map(id -> {
														  Long rank = redisTemplate.opsForZSet()
																				   .reverseRank(RANKING_KEY,
																								String.valueOf(id));
														  if (rank == null) {
															  // Redis에 없으면 정합성 문제로 예외 처리
															  throw new CustomException(ErrorCode.RANKING_NOT_FOUND);
														  }
														  return new Object[] {id, rank + 1};
													  })
													  .sorted((a, b) -> Long.compare((Long)a[1],
																					 (Long)b[1])) // rankNo 기준 정렬
													  .skip(pageable.getOffset())
													  .limit(pageable.getPageSize())
													  .map(a -> {
														  Long userId = (Long)a[0];
														  Ranking ranking = rankingRepository.findByUser_Id(userId)
																							 .orElseThrow(
																								 () -> new CustomException(
																									 ErrorCode.RANKING_NOT_FOUND));
														  Long rankNo = (Long)a[1];
														  return RankingSummaryDto.toDto(ranking, rankNo);
													  })
													  .toList();

		Long totalCount = (long)candidateIds.size();

		return PageResponse.toDto(
			new PageImpl<>(dtoList, pageable, totalCount)
		);
	}

	@Override
	public RankingDto getRanking(Long userId) {
		Ranking ranking = rankingRepository.findByUser_Id(userId)
										   .orElseThrow(() -> new CustomException(ErrorCode.RANKING_NOT_FOUND));

		Long rank = redisTemplate.opsForZSet()
								 .reverseRank(RANKING_KEY, String.valueOf(userId));

		if (rank == null) {
			throw new CustomException(ErrorCode.RANKING_NOT_FOUND);
		}

		return RankingDto.toDto(ranking, rank + 1L);
	}
}
