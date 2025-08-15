package com.explorer.gabom.global.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.explorer.gabom.domain.notification.dto.request.SocketNotificationRequest;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

	private final NotificationService notificationService;


	@MessageMapping("/notifications")
	public void handleNotification(SocketNotificationRequest request) {

		NotificationType type = NotificationType.valueOf(request.getType());
		NotificationRefType refType = (request.getRefType() != null && !request.getRefType().isBlank())
									  ? NotificationRefType.valueOf(request.getRefType())
									  : null;

		// 알림 생성 및 WebSocket 발송까지 처리 (서비스 내부에서 전부 처리됨)
		notificationService.notify(
			request.getReceiverId(),
			NotificationType.valueOf(request.getType()),
			request.getMessage(),
			request.getLink(),
			refType,
			request.getRefId()
		);

		// 서비스에서 자동으로 /sub/notifications/{userId} 로 전송함
		// 별도 SimpMessagingTemplate 전송 코드 필요 없음
	}
}
