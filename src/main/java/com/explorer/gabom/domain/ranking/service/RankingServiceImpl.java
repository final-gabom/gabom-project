package com.explorer.gabom.domain.ranking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
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

		// Redis ZSet에서 해당 페이지 구간의 유저와 점수 조회
		Set<ZSetOperations.TypedTuple<String>> userScores =
			redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, start, end);

		long totalCount = redisTemplate.opsForZSet().size(rankingKey);

		List<RankingDto> rankingList = new ArrayList<>();
		int rankOffset = start + 1;

		// null 체크 불필요 → 빈 Set 여부만 확인
		if (!userScores.isEmpty()) {
			for (ZSetOperations.TypedTuple<String> tuple : userScores) {
				Long userId = Long.valueOf(tuple.getValue());
				int exp = tuple.getScore().intValue();

				// 유저 상세 정보 해시에서 조회
				String userKey = "ranking:user:" + userId;
				Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(userKey);

				rankingList.add(
					RankingDto.builder()
							  .rankNo(rankOffset++)
							  .userId(userId)
							  .nickname((String)userInfo.get("nickname"))
							  .profileImageId((String)userInfo.get("profileImageId"))
							  .level(Integer.parseInt((String)userInfo.get("level")))
							  .exp(exp)
							  .titleName((String)userInfo.get("titleName"))
							  .build()
				);
			}
		}

		// Page 객체로 변환 후 PageResponse 생성
		Page<RankingDto> rankingDtoPage = new PageImpl<>(rankingList, pageable, totalCount);
		return PageResponse.toDto(rankingDtoPage);
	}
}
