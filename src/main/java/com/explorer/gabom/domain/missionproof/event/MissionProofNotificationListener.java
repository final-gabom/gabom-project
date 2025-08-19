package com.explorer.gabom.domain.missionproof.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.explorer.gabom.domain.notification.event.MissionProofCreatedEvent;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionProofNotificationListener {
	private final NotificationService notificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(MissionProofCreatedEvent e) {
		log.info("[NOTI] (AFTER_COMMIT) AUTH_POST_CREATED → receiver={}, refId={}, link={}",
				 e.receiverId(), e.refId(), e.link());

		notificationService.notify(
			e.receiverId(),
			NotificationType.AUTH_POST_CREATED,
			e.message(),
			e.link(),
			e.refType(),
			e.refId()
		);
	}
}
