package com.explorer.gabom.domain.exploration.vo;

public class RewardCalculator {

	public static int calculate(double distanceKm) {
		for (RewardRange range : RewardRange.values()) {
			if (range.contains(distanceKm)) {
				return range.getRewardPoint();
			}
		}
		// fallback (예외처리용)
		throw new IllegalArgumentException("유효하지 않은 거리입니다.");
	}
}