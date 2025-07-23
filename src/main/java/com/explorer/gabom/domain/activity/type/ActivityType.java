package com.explorer.gabom.domain.activity.type;

public enum ActivityType {
	MISSION_PROOF_CREATED(true),
	PLACE_SHARED(true),
	QUEST_COMPLETED(true),
	TITLE_EARNED(true),
	USER_LOGIN(false);

	private final boolean requiredTargetId;

	ActivityType(boolean requiredTargetId) {
		this.requiredTargetId = requiredTargetId;
	}

	public boolean getRequiredTargetId() {
		return requiredTargetId;
	}
}
