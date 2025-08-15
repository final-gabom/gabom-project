package com.explorer.gabom.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.notification.dto.request.SendNotificationRequestDto;
import com.explorer.gabom.domain.notification.dto.response.NotificationResponseDto;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationTestController {
	private final NotificationService notificationService;

	@PostMapping("/send")
	public ResponseEntity<ApiResponse<NotificationResponseDto>> send(@RequestBody SendNotificationRequestDto req) {
		Notification saved = notificationService.notify(
			req.getReceiverId(),
			req.getType(),
			req.getMessage(),
			req.getLink(),
			req.getRefType(),
			req.getRefId()
		);
		return ResponseEntity.ok(
			ApiResponse.success("알림이 전송되었습니다.",
								NotificationResponseDto.toDto(saved))
		);
	}

}
