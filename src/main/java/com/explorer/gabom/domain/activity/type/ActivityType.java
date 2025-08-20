package com.explorer.gabom.domain.activity.type;

import lombok.Getter;

@Getter
public enum ActivityType {

	// 유저 활동
	// 인증
	AUTH_LOGIN(false, "로그인하였습니다."),

	// 유저
	PASSWORD_RESET(false, "비밀번호를 재설정하였습니다."),

	// 장소
	PLACE_SHARED(true, "장소를 등록하였습니다."),
	PLACE_UPDATED(true, "장소를 수정하였습니다."),
	PLACE_DELETED(true, "장소를 삭제하였습니다."),

	// 탐험
	START_EXPLORATION(true, "탐험을 시작하였습니다."),
	EXTEND_EXPLORATION_TIME(true, "탐험 시간을 연장하였습니다."),

	// 인증글
	MISSION_PROOF_CREATED(true, "인증글을 작성하였습니다."),
	MISSION_PROOF_UPDATED(true, "인증글을 수정하였습니다"),
	MISSION_PROOF_DELETED(true, "인증글을 삭제하였습니다"),

	// 퀘스트
	QUEST_REWARD_CLAIMED(false, "퀘스트 보상을 수령하였습니다."),

	// 칭호
	TITLE_UPDATED(false, "칭호를 변경하였습니다."),

	// 관리자 활동
	// 퀘스트
	ADMIN_QUEST_CREATED(true, "퀘스트를 등록하였습니다."),
	ADMIN_QUEST_UPDATED(true, "퀘스트를 수정하였습니다."),
	ADMIN_QUEST_DELETED(true, "퀘스트를 삭제하였습니다."),

	// 칭호
	ADMIN_TITLE_CREATED(true, "칭호를 등록하였습니다."),
	ADMIN_TITLE_UPDATED(true, "칭호를 수정하였습니다."),
	ADMIN_TITLE_DELETED(true, "칭호를 삭제하였습니다.");

	private final boolean requiredTargetId;
	private final String message;

	ActivityType(boolean requiredTargetId, String message) {
		this.requiredTargetId = requiredTargetId;
		this.message = message;
	}

}
