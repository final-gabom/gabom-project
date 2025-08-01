package com.explorer.gabom.domain.place.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExploreRadius {

	RANGE_0_3 (0, 3),               // 0 km 이상 ~ 3 km 미만
	RANGE_3_5 (3, 5),               // 3 km 이상 ~ 5 km 미만
	RANGE_5_PLUS (5, Double.MAX_VALUE);   // 5 km 이상

	private final double minKm;
	private final double maxKm;

	public double getMinMeters() { return minKm * 1_000; }
	public double getMaxMeters() { return maxKm * 1_000; }
}
