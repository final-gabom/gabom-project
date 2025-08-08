package com.explorer.gabom.domain.notification.type;

public enum NotificationType {
	MISSION_PROOF_CREATED, // 인증글이 생성되었을 때
	QUEST_COMPLETED,       // 퀘스트가 완료되었을 때
	POINT_GRANTED,         // 포인트가 지급되었을 때
	QUEST_EXPIRED, // 퀘스트 시간이 만료되었을 때
	QUEST_TIME_RUNNING_OUT, // 퀘스트 만료시간 임박할 때
	ALERT // 테스트용
}
