package com.explorer.gabom.domain.exploration.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.global.dto.TargetIdentifiable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "탐험 시작 응답 정보")
public class ExplorationStartResponse implements TargetIdentifiable {

	@Schema(description = "탐험 ID", example = "1")
	private Long explorationId;

	@Schema(description = "리워드 포인트", example = "100")
	private int rewardPoint;

	@Schema(description = "리워드 경험치", example = "100")
	private int rewardExp;

	@Schema(description = "탐험 시작 시각", example = "2025-08-05T15:00:00")
	private LocalDateTime startAt;

	@Schema(description = "탐험 종료 시각", example = "2025-08-05T18:00:00")
	private LocalDateTime endAt;

	@Override
	public Long getTargetId() {
		return this.explorationId;
	}
}
