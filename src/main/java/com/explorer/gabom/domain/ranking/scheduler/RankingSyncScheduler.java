package com.explorer.gabom.domain.ranking.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingSyncScheduler {

	private final StringRedisTemplate redisTemplate;
	private final RankingRepository rankingRepository;
	private final TitleRepository titleRepository;
	private final AttachmentFileRepository attachmentFileRepository;

	@Scheduled(fixedRate = 300000)
	@Transactional
	public void syncRankingFromRedis() {
		String rankingKey = "ranking:exp";

		Set<ZSetOperations.TypedTuple<String>> entries =
			Objects.requireNonNullElse(
				redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, -1),
				Collections.emptySet()
			);

		if (entries.isEmpty()) {
			return;
		}

		// userIds 추출
		List<Long> userIds = entries.stream()
									.map(e -> Long.valueOf(e.getValue()))
									.toList();

		// 기존 랭킹 한번에 조회
		Map<Long, Ranking> existingRankings = rankingRepository.findByUserIdIn(userIds)
															   .stream()
															   .collect(Collectors.toMap(Ranking::getUserId, r -> r));

		// Redis에서 유저별 titleId, profileImageId 수집
		Set<Long> titleIds = new HashSet<>();
		Set<String> profileImageIds = new HashSet<>();

		Map<Long, Map<Object, Object>> userInfoMap = new HashMap<>();

		for (Long userId : userIds) {
			String userKey = "ranking:user:" + userId;
			Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(userKey);
			userInfoMap.put(userId, userInfo);

			Long titleId = parseLongSafe((String)userInfo.get("titleId"));
			if (titleId != null) {
				titleIds.add(titleId);
			}

			String profileImageId = parseStringSafe((String)userInfo.get("profileImageId"), null);
			if (profileImageId != null) {
				profileImageIds.add(profileImageId);
			}
		}

		Map<Long, Title> titleMap = titleRepository.findByIdIn(titleIds)
												   .stream()
												   .collect(Collectors.toMap(Title::getId, Function.identity()));

		Map<String, AttachmentFile> profileImageMap = attachmentFileRepository.findByFileIdIn(profileImageIds)
																			  .stream()
																			  .collect(Collectors.toMap(
																				  AttachmentFile::getFileId,
																				  Function.identity()));

		// Ranking 객체 생성 및 업데이트
		List<Ranking> rankingsToSave = new ArrayList<>();
		int rankNo = 1;

		for (ZSetOperations.TypedTuple<String> entry : entries) {
			Long userId = Long.valueOf(entry.getValue());
			int exp = entry.getScore().intValue();

			Map<Object, Object> userInfo = userInfoMap.get(userId);

			String nickname = parseStringSafe((String)userInfo.get("nickname"), "Unknown");
			int level = parseIntSafe((String)userInfo.get("level"), 0);

			Title title = null;
			Long titleId = parseLongSafe((String)userInfo.get("titleId"));
			if (titleId != null) {
				title = titleMap.get(titleId);
			}

			AttachmentFile profileImage = null;
			String profileImageId = parseStringSafe((String)userInfo.get("profileImageId"), null);
			if (profileImageId != null) {
				profileImage = profileImageMap.get(profileImageId);
			}

			Ranking ranking = existingRankings.getOrDefault(
				userId,
				new Ranking(userId, rankNo, level, exp, nickname, title, profileImage)
			);

			ranking.update(rankNo, exp, level, nickname, title, profileImage);

			rankingsToSave.add(ranking);
			rankNo++;
		}

		rankingRepository.saveAll(rankingsToSave);
	}

	private int parseIntSafe(String value, int defaultValue) {
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private Long parseLongSafe(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String parseStringSafe(String value, String defaultValue) {
		return (value == null || value.isEmpty()) ? defaultValue : value;
	}

}
