package com.explorer.gabom.domain.exploration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationCurrentResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationExtendTimeResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.exploration.service.ExplorationService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exploration")
@RequiredArgsConstructor
public class ExplorationController implements ExplorationControllerDocs {

	private final ExplorationService explorationService;

	// 탐험 시작
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

	// 탐험 중인 장소 조회
	@GetMapping("/current")
	public ResponseEntity<ApiResponse<ExplorationCurrentResponse>> getCurrentExploration(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		ExplorationCurrentResponse response = explorationService.getCurrentExploration(userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("현재 탐험 중인 장소 조회에 성공했습니다.", response));
	}

	// 탐험 제한 시간 연장
	@PatchMapping("/{explorationId}/extend-time")
	public ResponseEntity<ApiResponse<ExplorationExtendTimeResponse>> extendExplorationTime(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long explorationId
	) {
		Long userId = userDetails.getUserId();
		ExplorationExtendTimeResponse response = explorationService.extendExplorationTime(userId,
																						  explorationId);
		return ResponseEntity.ok(ApiResponse.success("탐험 제한 시간이 연장되었습니다.", response));
	}
}
