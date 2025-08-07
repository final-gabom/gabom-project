package com.explorer.gabom.global.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.explorer.gabom.domain.notification.dto.NotificationResponseDto;
import com.explorer.gabom.domain.notification.dto.SocketNotificationRequest;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final NotificationService notificationService;

	/**
	 * 클라이언트 → 서버: WebSocket 메시지 수신 핸들러
	 * 메시지 전송 경로: /app/notifications
	 */

	public void sendNotification(Long receiverId, NotificationResponseDto responseDto) {
		messagingTemplate.convertAndSend("/sub/notification/" + receiverId, responseDto);
	}

	@MessageMapping("/notifications")
	public void handleNotification(SocketNotificationRequest request) {
		// 알림 생성 및 WebSocket 발송까지 처리 (서비스 내부에서 전부 처리됨)
		Notification notification = notificationService.notify(
			request.getReceiverId(),
			NotificationType.valueOf(request.getType()),
			request.getMessage(),
			request.getLink()
		);

		// 서비스에서 자동으로 /sub/notifications/{userId} 로 전송함
		// 별도 SimpMessagingTemplate 전송 코드 필요 없음
	}
}
