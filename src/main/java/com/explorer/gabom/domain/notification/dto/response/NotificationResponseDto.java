package com.explorer.gabom.domain.notification.dto.response;

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

	private String refType;
	private Long refId;

	public static NotificationResponseDto toDto(Notification n) {
		return NotificationResponseDto.builder()
									  .id(n.getId())
									  .message(n.getMessage())
									  .link(n.getLink())
									  .createdAt(n.getCreatedAt())
									  .type(n.getType() != null ? n.getType().name() : null)
									  .isRead(n.isRead())
									  // 필요 시 제목/내용 가공해서 채워도 됨
									  .title(null)
									  .content(null)
									  // enum → 문자열 (null 안전)
									  .refType(n.getRefType() != null ? n.getRefType().name() : null)
									  .refId(n.getRefId())
									  .build();
	}
}
