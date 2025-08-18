package com.explorer.gabom.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.notification.entity.Notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(name = "NotificationResponseDto", description = "알림 조회 응답 DTO")
public class NotificationResponseDto {
	@Schema(description = "알림 ID", example = "124")
	private Long id;

	@Schema(description = "알림 메시지", example = "네 인증글에 좋아요가 눌렸습니다.")
	private String message;

	@Schema(description = "알림 클릭 시 이동 링크", example = "/proofs/77")
	private String link;

	@Schema(description = "알림 생성 시각", example = "2025-08-18T12:35:10")
	private LocalDateTime createdAt;

	@Schema(description = "알림 타입", example = "LIKE")
	private String type;

	@Schema(description = "읽음 여부", example = "false")
	private boolean isRead;

	@Schema(description = "제목(추후 확장용)", example = "null")
	private String title;

	@Schema(description = "내용(추후 확장용)", example = "null")
	private String content;

	@Schema(description = "참조 타입", example = "AUTH_POST")
	private String refType;

	@Schema(description = "참조 리소스 ID", example = "77")
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
