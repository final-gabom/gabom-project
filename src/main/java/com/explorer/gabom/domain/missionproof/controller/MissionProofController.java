package com.explorer.gabom.domain.missionproof.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.service.MissionProofService;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission-proof")
public class MissionProofController implements MissionProofControllerDocs {

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

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CreateMissionProofResponse>> updateMissionProof(
		@PathVariable Long id,
		@RequestBody UpdateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CreateMissionProofResponse response = missionProofService.updateMissionProof(id, request,
																					 userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("수정 성공", response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteMissionProof(
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		missionProofService.deleteMissionProof(id, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
	}

	@GetMapping("/{missionProofId}")
	public ResponseEntity<ApiResponse<MissionProofDetailResponse>> getMissionProofDetail(
		@PathVariable Long missionProofId
	) {
		MissionProofDetailResponse response = missionProofService.getMissionProofDetail(missionProofId);
		return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<MissionProofSummary>>> getMissionProofList(
		@RequestParam(value = "type", required = false) MissionProofType type,
		@RequestParam(value = "id", required = false) Long targetId,
		@RequestParam(value = "userId", required = false) Long userId,
		@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
		) {
		MissionProofSearchCondition condition = new MissionProofSearchCondition(type, targetId, userId);
		PageResponse<MissionProofSummary> response = missionProofService.getMissionProofs(condition, pageable);
		return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
	}
}
