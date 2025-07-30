package com.explorer.gabom.domain.Exploration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.Exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.Exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.Exploration.service.ExplorationService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exploration")
@RequiredArgsConstructor
public class ExplorationController {

	private final ExplorationService explorationService;

	@PostMapping("/{placeId}/start")
	public ResponseEntity<ApiResponse<ExplorationStartResponse>> startExploration(
		@PathVariable Long placeId,
		@RequestBody ExplorationStartRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		ExplorationStartResponse response = explorationService.startExploration(userId, placeId, request);
		return ResponseEntity.ok(ApiResponse.success("탐험이 시작되었습니다.", response));
	}
}
