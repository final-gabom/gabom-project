package com.explorer.gabom.domain.activity.type;

import lombok.Getter;

@Getter
public enum ActivityType {

	// 유저 활동
	MISSION_PROOF_CREATED(true, "인증글을 작성하였습니다."),
	PLACE_SHARED(true, "장소를 등록하였습니다."),
	QUEST_COMPLETED(true, "퀘스트를 달성하였습니다."),
	TITLE_EARNED(true, "칭호를 획득하였습니다."),
	AUTH_LOGIN(false, "로그인하였습니다."),

	// 관리자 활동
	ADMIN_QUEST_CREATED(true, "퀘스트를 등록하였습니다."),
	ADMIN_QUEST_UPDATED(true, "퀘스트를 수정하였습니다."),
	ADMIN_QUEST_DELETED(true, "퀘스트를 삭제하였습니다.");

	private final boolean requiredTargetId;
	private final String message;

	ActivityType(boolean requiredTargetId, String message) {
		this.requiredTargetId = requiredTargetId;
		this.message = message;
	}

}
