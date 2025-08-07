// package com.explorer.gabom.domain.notification.controller;
//
// import static org.springframework.data.domain.Sort.Direction.*;
//
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.explorer.gabom.domain.notification.dto.NotificationResponseDto;
// import com.explorer.gabom.domain.notification.service.NotificationService;
// import com.explorer.gabom.domain.notification.type.NotificationType;
// import com.explorer.gabom.global.dto.ApiResponse;
// import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/test")
// public class NotificationTestController {
//
// 	private final NotificationService notificationService;
//
// 	@PostMapping("/notify")
// 	public ResponseEntity<Void> sendTest(@RequestParam Long userId) {
// 		notificationService.notify(userId, NotificationType.ALERT, "✅ 서버에서 보낸 테스트 메시지", "http://example.com");
// 		return ResponseEntity.ok().build();
// 	}
//
//
// }
