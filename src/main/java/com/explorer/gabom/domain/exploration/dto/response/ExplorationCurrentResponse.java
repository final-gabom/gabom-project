package com.explorer.gabom.domain.exploration.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.exploration.entity.Exploration;
import com.explorer.gabom.domain.place.entity.Place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "현재 진행 중인 탐험 응답 정보")
public class ExplorationCurrentResponse {

	@Schema(description = "탐험 ID", example = "1")
	private Long explorationId;

	@Schema(description = "탐험 중인 장소 ID", example = "101")
	private Long placeId;

	@Schema(description = "탐험 장소 제목", example = "한강 시민공원")
	private String placeTitle;

	@Schema(description = "탐험 시작 시각", example = "2025-08-05T14:30:00")
	private LocalDateTime startedAt;

	@Schema(description = "탐험 제한 시간 (종료 예정 시각)", example = "2025-08-05T17:30:00")
	private LocalDateTime deadline;

	@Schema(description = "탐험 성공 시 리워드 포인트", example = "50")
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