package com.explorer.gabom.domain.Exploration.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExplorationStartResponse {

	private Long explorationId;
	private int rewardPoint;
	private int rewardExp;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

}
