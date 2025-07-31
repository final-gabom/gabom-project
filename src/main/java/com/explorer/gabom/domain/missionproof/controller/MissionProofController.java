package com.explorer.gabom.domain.missionproof.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.service.MissionProofService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission-proof")
public class MissionProofController {

	private final MissionProofService missionProofService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateMissionProofResponse>> createMissionProof(
		@Valid @RequestBody CreateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CreateMissionProofResponse response = missionProofService.createMissionProof(request, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("미션 인증이 완료되었습니다.", response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteMissionProof(
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		missionProofService.deleteMissionProof(id, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
	}
}
