package com.explorer.gabom.domain.notification.event;

// 인증글 생성 시 이벤트로 사용할 클래스
// 수신자 ID, 메시지, 링크 전달
public record MissionProofCreatedEvent(Long receiverId, String message, String link) {
}
