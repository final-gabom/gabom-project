package com.explorer.gabom.domain.exploration.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.exploration.entity.Exploration;
import com.explorer.gabom.domain.place.entity.Place;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExplorationCurrentResponse {

	private Long explorationId;
	private Long placeId;
	private String placeTitle;
	private LocalDateTime startedAt;
	private LocalDateTime deadline;
	private int rewardPoint;

	public static ExplorationCurrentResponse of(Exploration exploration, Place place) {
		ExplorationCurrentResponse response = new ExplorationCurrentResponse();
		response.explorationId = exploration.getId();
		response.placeId = place.getId();
		response.placeTitle = place.getTitle();
		response.startedAt = exploration.getStartAt();
		response.deadline = exploration.getEndAt();
		response.rewardPoint = exploration.getRewardPoint();
		return response;
	}
}
