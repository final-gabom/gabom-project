package com.explorer.gabom.domain.exploration.type;

public enum RewardRange {

	RANGE_1_3(1, 3, 50),
	RANGE_3_5(3, 5, 100),
	RANGE_5_10(5, 10, 150),
	RANGE_10_15(10, 15, 200),
	RANGE_15_20(15, 20, 250),
	RANGE_OVER_20(20, Double.MAX_VALUE, 300);

	private final double minKm;
	private final double maxKm;
	private final int rewardPoint;

	RewardRange(double minKm, double maxKm, int rewardPoint) {
		this.minKm = minKm;
		this.maxKm = maxKm;
		this.rewardPoint = rewardPoint;
	}

	public boolean contains(double distance) {
		return distance >= minKm && distance < maxKm;
	}

	public int getRewardPoint() {
		return rewardPoint;
	}
}
