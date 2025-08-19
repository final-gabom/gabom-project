package com.explorer.gabom.domain.notification.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.notification.dto.request.SocketNotificationRequest;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification (Dev)", description = "알림 조회 및 테스트 전송 (개발용)")
public interface NotificationControllerDocs {

	@Operation(summary = "내 알림 목록 조회", description = "로그인 유저의 알림을 페이징으로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	ResponseEntity<?> getMyNotifications(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		Pageable pageable
	);

	@Operation(summary = "테스트 알림 전송", description = "특정 사용자에게 알림을 생성/전송합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "전송 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값 오류"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "수신자 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	ResponseEntity<?> sendTestNotification(
		@RequestBody SocketNotificationRequest request
	);
}
