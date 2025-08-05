package com.explorer.gabom.domain.exploration.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "탐험 제한 시간 연장 응답 정보")
public class ExplorationExtendTimeResponse {

	@Schema(description = "탐험 ID", example = "1")
	private Long explorationId;

	@Schema(description = "연장된 탐험 종료 시각", example = "2025-08-05T21:00:00")
	private LocalDateTime newDeadline;
}