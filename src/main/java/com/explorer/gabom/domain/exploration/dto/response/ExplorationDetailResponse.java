package com.explorer.gabom.domain.exploration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.exploration.entity.Exploration;

@AllArgsConstructor
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

	public static ExplorationDetailResponse toDto(Exploration exploration) {
		return new ExplorationDetailResponse(
			exploration.getId(),
			exploration.getUser().getId(),
			exploration.getUser().getNickname(),
			exploration.getPlace().getId(),
			exploration.getPlace().getTitle(),
			exploration.getRewardPoint(),
			exploration.getRewardExp(),
			exploration.getStartAt(),
			exploration.getEndAt()
		);
	}
}
