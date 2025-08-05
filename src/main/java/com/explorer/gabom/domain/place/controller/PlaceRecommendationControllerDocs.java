package com.explorer.gabom.domain.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.domain.place.type.ExploreRadius;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "PlaceRecommendationAPI",
	description = "탐험 장소 추천 관련 기능을 제공합니다."
)
public interface PlaceRecommendationControllerDocs {

	@Operation(
		summary = "탐험 장소 추천",
		description = "지정된 반경 내에서 랜덤으로 탐험 장소를 추천합니다.  \n"
			+ "- 위도(lat), 경도(lng), 반경(radius)을 기반으로 장소 목록을 조회합니다.  \n"
			+ "- 반경은 Enum 값(`RANGE_0_3`, `RANGE_3_5`, `RANGE_5_PLUS`)으로 전달해야 하며,  \n"
			+ "- 기본적으로 4개의 장소를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "추천 장소 조회 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값")
	})
	ResponseEntity<?> getRecommendedPlaces(
		@Parameter(description = "현재 위치의 위도", example = "37.564043", required = true)
		@RequestParam double lat,

		@Parameter(description = "현재 위치의 경도", example = "126.820741", required = true)
		@RequestParam double lng,

		@Parameter(description = "탐험 반경 (RANGE_0_3 | RANGE_3_5 | RANGE_5_PLUS)", required = true)
		@RequestParam ExploreRadius radius
	);
}