package com.explorer.gabom.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "SocketNotificationRequest", description = "웹소켓/테스트 알림 요청 DTO")
public class SocketNotificationRequest {
	@Schema(description = "수신자 ID (알림 받을 사용자)", example = "16", requiredMode = Schema.RequiredMode.REQUIRED)
	private Long receiverId;
	@Schema(description = "알림 메시지", example = "네 장소에 인증글이 남겨졌습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
	private String message;
	@Schema(description = "알림 클릭 시 이동 링크", example = "/proofs/77")
	private String link;
	@Schema(description = "알림 타입", example = "LIKE",
		allowableValues = {"COMMENT","LIKE","REPLY","SYSTEM"})
	private String type;

	// 선택: 어떤 리소스에 대한 알림인지
	@Schema(description = "참조 리소스 타입", example = "AUTH_POST",
		allowableValues = {"AUTH_POST","EXPLORATION","SYSTEM"})
	private String refType;  // NotificationRefType.name(), 없으면 null/빈값
	@Schema(description = "참조 리소스 ID (해당 리소스 PK)", example = "77")
	private Long refId;      // 해당 리소스 PK, 없으면 null
}
