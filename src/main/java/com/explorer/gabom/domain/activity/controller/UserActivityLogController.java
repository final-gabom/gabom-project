package com.explorer.gabom.domain.activity.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.domain.activity.service.UserActivityLogService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity-logs")
public class UserActivityLogController {
	private final UserActivityLogService userActivityLogService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<PageResponse<UserActivityLogResponse>>> getMyLogs(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam(required = false) LocalDateTime from,
		@RequestParam(required = false) LocalDateTime to,
		@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	)  {
		PageResponse<UserActivityLogResponse> response = userActivityLogService.getMyLogs(customUserDetails.getUserId(), from, to, pageable);
		return ResponseEntity.ok(ApiResponse.success("활동 로그가 성공적으로 조회되었습니다.", response));
	}

}
