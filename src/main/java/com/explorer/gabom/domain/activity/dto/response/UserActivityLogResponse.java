package com.explorer.gabom.domain.activity.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.activity.entity.UserActivityLog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLogResponse {
	@Schema(description = "활동로그 ID")
	private Long id;
	@Schema(description = "로그인한 유저 ID")
	private Long userId;
	@Schema(description = "활동 유형")
	private String activityType;
	@Schema(description = "대상 ID")
	private Long targetId;
	@Schema(description = "설명")
	private String description;
	@Schema(description = "IP 주소")
	private String ipAddress;
	@Schema(description = "활동일")
	private LocalDateTime createdAt;

	public static UserActivityLogResponse toDto(UserActivityLog log) {
		return new UserActivityLogResponse(
			log.getId(),
			log.getUserId(),
			log.getActivityType().name(),
			log.getTargetId(),
			log.getDescription(),
			log.getIpAddress(),
			log.getCreatedAt()
		);
	}

}