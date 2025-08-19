package com.explorer.gabom.domain.notification.dto.request;

import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "SendNotificationRequestDto", description = "내부 서비스용 알림 생성 DTO")
public class SendNotificationRequestDto {
	@Schema(description = "수신자 ID", example = "16", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long receiverId;                 // 반드시: WebSocket에 접속한 사용자 ID
	@Schema(description = "알림 타입", example = "AUTH_POST_CREATED",
		allowableValues = {"AUTH_POST_CREATED", "QUEST_EXPIRED", "LIKE", "COMMENT", "SYSTEM"})
	private NotificationType type;           // 예) AUTH_POST_CREATED, QUEST_EXPIRED ...
	@Schema(description = "알림 메시지", example = "탐험 퀘스트가 만료되었습니다.")
	private String message;                  // 표시 메시지
	@Schema(description = "알림 클릭 시 이동 링크", example = "/explorations/12")
	private String link;                     // 이동 링크(없으면 null)

	// 선택: 어떤 리소스에 대한 알림인지
	@Schema(description = "참조 타입", example = "EXPLORATION",
		allowableValues = {"AUTH_POST", "EXPLORATION", "SYSTEM"})
	private NotificationRefType refType;     // (예) AUTH_POST, EXPLORATION, SYSTEM ...
	@Schema(description = "참조 리소스 ID", example = "12")
	private Long refId;
}
