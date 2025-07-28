package com.explorer.gabom.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.user.dto.response.UserBlockResponse;
import com.explorer.gabom.domain.user.service.UserBlockService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserBlockController {

	private final UserBlockService userBlockService;

	@PostMapping("/block/{userId}")
	public ResponseEntity<ApiResponse<UserBlockResponse>>blockUser(
		@PathVariable("userId") Long blockedUserId,
		@AuthenticationPrincipal CustomUserDetails userDetails){
		Long blockerId = userDetails.getUserId();
		UserBlockResponse response = userBlockService.blockUser(blockerId, blockedUserId);
		return ResponseEntity.ok(ApiResponse.success("유저를 차단했습니다.", response));
	}
}
