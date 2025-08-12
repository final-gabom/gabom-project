package com.explorer.gabom.domain.ranking.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final StringRedisTemplate redisTemplate;

	@Override
	public PageResponse<RankingDto> getRankingPage(Pageable pageable) {
		String rankingKey = "ranking:exp";

		int start = (int)pageable.getOffset();
		int end = start + pageable.getPageSize() - 1;

		Set<ZSetOperations.TypedTuple<String>> userScores =
			Objects.requireNonNullElse(
				redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, start, end),
				Collections.emptySet()
			);

		long totalCount =
			Objects.requireNonNullElse(
				redisTemplate.opsForZSet().size(rankingKey),
				0L
			);

		AtomicInteger rankNo = new AtomicInteger(start + 1);

		List<RankingDto> rankingList = userScores.stream()
												 .map(tuple -> {
													 Long userId = Long.valueOf(tuple.getValue());
													 int exp = tuple.getScore().intValue();

													 Map<Object, Object> userInfo = redisTemplate.opsForHash()
																								 .entries(
																									 "ranking:user:"
																										 + userId);

													 return RankingDto.builder()
																	  .rankNo(rankNo.getAndIncrement())
																	  .userId(userId)
																	  .nickname((String)userInfo.get("nickname"))
																	  .profileImgUrl(
																		  (String)userInfo.get("profileImageUrl"))
																	  .level(toInt(userInfo.get("level")))
																	  .exp(exp)
																	  .titleId(toLong(userInfo.get("titleId")))
																	  .build();
												 })
												 .toList();

		return PageResponse.toDto(new PageImpl<>(rankingList, pageable, totalCount));
	}

	private Long toLong(Object value) {
		if (value == null)
			return null;
		try {
			return Long.parseLong(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer toInt(Object value) {
		if (value == null)
			return null;
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
