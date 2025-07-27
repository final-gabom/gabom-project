package com.explorer.gabom.domain.title.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.service.UserTitleService;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/titles")
public class UserTitleController {
	private final UserTitleService userTitleService;

	@GetMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<List<UserTitleResponse>>> getUserTitles(
		@AuthenticationPrincipal Long userId) {
		List<UserTitleResponse> titles = userTitleService.getUserTitles(userId);
		return ResponseEntity.ok(ApiResponse.success("칭호가 성공적으로 조회되었습니다.", titles));
	}
}
