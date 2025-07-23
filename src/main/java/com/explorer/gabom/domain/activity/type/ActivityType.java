package com.explorer.gabom.domain.activity.type;

import lombok.Getter;

@Getter
public enum ActivityType {
	MISSION_PROOF_CREATED(true, "인증글을 작성하였습니다."),

	PLACE_SHARED(true, "장소를 등록하였습니다."),

	QUEST_COMPLETED(true, "퀘스트를 달성하였습니다."),

	TITLE_EARNED(true, "칭호를 획득하였습니다."),

	AUTH_LOGIN(false, "로그인하였습니다.");

	private final boolean requiredTargetId;
	private final String message;

	ActivityType(boolean requiredTargetId, String message) {
		this.requiredTargetId = requiredTargetId;
		this.message = message;
	}
}
