package com.explorer.gabom.domain.notification.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketNotificationRequest {
	private Long receiverId;
	private String message;
	private String link;
	private String type;

	// 선택: 어떤 리소스에 대한 알림인지
	private String refType;  // NotificationRefType.name(), 없으면 null/빈값
	private Long refId;      // 해당 리소스 PK, 없으면 null
}
