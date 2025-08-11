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

			String titleIdStr = (String)userInfo.get("titleId");
			if (titleIdStr != null && !titleIdStr.isEmpty()) {
				try {
					titleIds.add(Long.valueOf(titleIdStr));
				} catch (NumberFormatException ignored) {
				}
			}

			String profileImageId = (String)userInfo.get("profileImageId");
			if (profileImageId != null && !profileImageId.isEmpty()) {
				profileImageIds.add(profileImageId);
			}
		}

		// 배치 조회 (한 번씩만 DB 조회)
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

			String nickname = (String)userInfo.get("nickname");
			String titleIdStr = (String)userInfo.get("titleId");
			String profileImageId = (String)userInfo.get("profileImageId");
			int level = Integer.parseInt((String)userInfo.get("level"));

			Title title = null;
			if (titleIdStr != null && !titleIdStr.isEmpty()) {
				try {
					Long tid = Long.valueOf(titleIdStr);
					title = titleMap.get(tid);
				} catch (NumberFormatException ignored) {
				}
			}

			AttachmentFile profileImage = null;
			if (profileImageId != null && !profileImageId.isEmpty()) {
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

		// 일괄 저장
		rankingRepository.saveAll(rankingsToSave);
	}
}
