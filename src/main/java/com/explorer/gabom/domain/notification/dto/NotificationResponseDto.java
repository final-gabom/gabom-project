package com.explorer.gabom.domain.notification.dto;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NotificationResponseDto {
	private Long id;
	private String message;
	private String link;
	private LocalDateTime createdAt;
	private String type;
	private boolean isRead;
	private String title;
	private String content;

	public static NotificationResponseDto from(Notification n) {
		return NotificationResponseDto.builder()
									  .id(n.getId())
									  .message(n.getMessage())
									  .link(n.getLink())
									  .createdAt(n.getCreatedAt())
									  .type(n.getType().name())
									  .isRead(n.isRead())
									  .title(null)    // 필요 시 수정
									  .content(null)  // 필요 시 수정
									  .build();
	}
}
