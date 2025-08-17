package com.explorer.gabom.domain.exploration.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExplorationDetailResponse {
	private final Long explorationId;
	private final Long userId;
	private final String userNickname;
	private final Long placeId;
	private final String placeTitle;
	private final int rewardPoint;
	private final int rewardExp;
	private final LocalDateTime startAt;
	private final LocalDateTime endAt;

	public ExplorationDetailResponse(Long explorationId,
									 Long userId,
									 String userNickname,
									 Long placeId,
									 String placeTitle,
									 int rewardPoint,
									 int rewardExp,
									 LocalDateTime startAt,
									 LocalDateTime endAt) {
		this.explorationId = explorationId;
		this.userId = userId;
		this.userNickname = userNickname;
		this.placeId = placeId;
		this.placeTitle = placeTitle;
		this.rewardPoint = rewardPoint;
		this.rewardExp = rewardExp;
		this.startAt = startAt;
		this.endAt = endAt;
	}
}
