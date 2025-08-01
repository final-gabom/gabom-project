package com.explorer.gabom.domain.place.dto.request;

import com.explorer.gabom.domain.place.type.ExploreRadius;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceRecommendationRequestDto {
	private ExploreRadius radius;
	private double lat;
	private double lng;
}
