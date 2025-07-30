package com.explorer.gabom.domain.activity.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.activity.entity.UserActivityLog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLogResponse {
	private Long id;
	private Long userId;
	private String activityType;
	private Long targetId;
	private String description;
	private String ipAddress;
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