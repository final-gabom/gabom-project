package com.explorer.gabom.domain.notification.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.notification.event.MissionProofCreatedEvent;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

	private final NotificationService notificationService;

	// 인증글 생성 이벤트 발생 시 호출
	@EventListener
	public void handleMissionProofCreated(MissionProofCreatedEvent event) {
		notificationService.notify(
			// 이벤트 정보를 기반으로 알림 생성/저장
			event.receiverId(),
			NotificationType.MISSION_PROOF_CREATED,
			event.message(),
			event.link(),
			event.refType(),   // record 접근자 (enum 그대로 전달)
			event.refId()      // record 접근자
		);

	}
}
