package com.explorer.gabom.domain.ranking.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingSyncScheduler {

	private final StringRedisTemplate redisTemplate;
	private final RankingRepository rankingRepository;

	@Scheduled(fixedRate = 300000) // 5분마다 실행
	@Transactional
	public void syncRankingFromRedis() {
		String rankingKey = "ranking:exp";

		// Redis에서 랭킹 전체 가져오기
		Set<ZSetOperations.TypedTuple<String>> entries =
			redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, -1);

		if (entries.isEmpty()) {
			return; // 데이터 없으면 종료
		}

		// Redis에서 조회한 userId 목록
		List<Long> userIds = entries.stream()
									.map(e -> Long.valueOf(e.getValue()))
									.toList();

		// DB에서 기존 랭킹 데이터 한 번에 조회
		Map<Long, Ranking> existingRankings = rankingRepository.findByUserIdIn(userIds)
															   .stream()
															   .collect(Collectors.toMap(Ranking::getUserId, r -> r));

		List<Ranking> rankingsToSave = new ArrayList<>();

		for (ZSetOperations.TypedTuple<String> entry : entries) {
			Long userId = Long.valueOf(entry.getValue());
			int exp = entry.getScore().intValue();

			String userKey = "ranking:user:" + userId;
			Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(userKey);

			String nickname = (String)userInfo.get("nickname");
			String titleName = (String)userInfo.get("titleName");
			String profileImageId = (String)userInfo.get("profileImageId");
			int level = Integer.parseInt((String)userInfo.get("level"));

			Ranking ranking = existingRankings.getOrDefault(
				userId,
				new Ranking(userId, null, level, exp, nickname, titleName, profileImageId)
			);

			ranking.update(exp, level, nickname, titleName, profileImageId);
			rankingsToSave.add(ranking);
		}

		// 한 번에 저장
		rankingRepository.saveAll(rankingsToSave);
	}
}
