package com.explorer.gabom.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketNotificationRequest {
	private Long receiverId;
	private String message;
	private String link;
	private String type;
}
