package com.explorer.gabom.domain.notification.event;

import com.explorer.gabom.domain.notification.type.NotificationRefType;

// 인증글 생성 시 이벤트로 사용할 클래스
// 수신자 ID, 메시지, 링크 전달
public record MissionProofCreatedEvent(
	Long receiverId, String message, String link, NotificationRefType refType, Long refId  ) {
	// 편의 팩토리: 인증글 생성 전용
	public static MissionProofCreatedEvent ofAuthPost(
		Long receiverId, String message, String link, Long authPostId
	) {
		return new MissionProofCreatedEvent(
			receiverId, message, link, NotificationRefType.AUTH_POST, authPostId
		);
	}
}
