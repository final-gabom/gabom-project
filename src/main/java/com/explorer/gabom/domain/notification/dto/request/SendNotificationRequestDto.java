package com.explorer.gabom.domain.notification.dto.request;

import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendNotificationRequestDto {
	private Long receiverId;                 // 반드시: WebSocket에 접속한 사용자 ID
	private NotificationType type;           // 예) AUTH_POST_CREATED, QUEST_EXPIRED ...
	private String message;                  // 표시 메시지
	private String link;                     // 이동 링크(없으면 null)

	// 선택: 어떤 리소스에 대한 알림인지
	private NotificationRefType refType;     // 예) AUTH_POST, EXPLORATION, SYSTEM ...
	private Long refId;
}
