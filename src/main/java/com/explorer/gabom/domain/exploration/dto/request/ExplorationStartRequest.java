package com.explorer.gabom.domain.exploration.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "탐험 시작 요청 정보")
public class ExplorationStartRequest {

	@Schema(description = "현재 사용자의 위도", example = "37.5665")
	private double lat;

	@Schema(description = "현재 사용자의 경도", example = "126.9780")
	private double lng;
}