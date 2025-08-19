package com.explorer.gabom.domain.notification.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.notification.dto.response.NotificationResponseDto;
import com.explorer.gabom.domain.notification.dto.request.SocketNotificationRequest;
import com.explorer.gabom.domain.notification.entity.Notification;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationType;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.explorer.gabom.global.websocket.NotificationSocketController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/dev")
public class NotificationController implements NotificationControllerDocs {
	private final NotificationService notificationService;
	private final NotificationSocketController socketController;

	// [1] 알림 목록 조회 (페이징 지원)
	@GetMapping
	public ResponseEntity<ApiResponse<?>> getMyNotifications(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {

		Page<NotificationResponseDto> response = notificationService.getNotifications(userDetails.getUserId(),
																					  pageable);
		return ResponseEntity.ok(ApiResponse.success("조회 완료 되었습니다.", response));
	}

	@PostMapping("/send")
	public ResponseEntity<ApiResponse<?>> sendTestNotification(@RequestBody SocketNotificationRequest request) {
		Notification saved = notificationService.notify(
			request.getReceiverId(),
			NotificationType.valueOf(request.getType()),
			request.getMessage(),
			request.getLink()
		);

		return ResponseEntity.ok(ApiResponse.success("알림 전송 완료", NotificationResponseDto.toDto(saved)));
	}
}
