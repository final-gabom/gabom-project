package com.explorer.gabom.domain.notification.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.notification.event.MissionProofCreatedEvent;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MissionProofEventListener {

	private final NotificationService notificationService;

	// 인증글 생성 이벤트 수신 → 알림 저장 + WebSocket 전송
	@EventListener
	public void handle(MissionProofCreatedEvent event) {
		notificationService.notify(
			event.receiverId(),
			NotificationType.AUTH_POST_CREATED, // 없으면 enum에 추가
			event.message(),
			event.link(),
			event.refType(),
			event.refId()
		);
	}
}
