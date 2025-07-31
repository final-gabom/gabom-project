package com.explorer.gabom.domain.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.place.dto.request.PlaceRecommendationRequestDto;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.service.PlaceRecommendationService;
import com.explorer.gabom.domain.place.type.ExploreRadius;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceRecommendationController {

	private final PlaceRecommendationService recommendationService;

	@GetMapping("/recommendations")
	public ResponseEntity<ApiResponse<List<PlaceSummary>>> getRecommendedPlaces(@RequestParam double lat,
																				@RequestParam double lng,
																				@RequestParam ExploreRadius radius) {

		PlaceRecommendationRequestDto requestDto = PlaceRecommendationRequestDto.builder()
																				.lat(lat)
																				.lng(lng)
																				.radius(radius)
																				.build();

		List<PlaceSummary> response = recommendationService.getRecommendedPlaces(requestDto);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("장소 추천 조회 성공", response));
	}
}
