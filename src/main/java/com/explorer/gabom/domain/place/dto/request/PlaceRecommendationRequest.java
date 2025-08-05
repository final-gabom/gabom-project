package com.explorer.gabom.domain.place.dto.request;

import com.explorer.gabom.domain.place.type.ExploreRadius;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Getter
@Schema(description = "탐험 장소 추천 요청 정보")
public class PlaceRecommendationRequest {

	@Schema(
		description = "탐험 반경 (RANGE_0_3, RANGE_3_5, RANGE_5_PLUS 중 하나)",
		example = "RANGE_0_3"
	)
	private ExploreRadius radius;

	@Schema(description = "현재 위치의 위도", example = "37.564043")
	private double lat;

	@Schema(description = "현재 위치의 경도", example = "126.820741")
	private double lng;
}