package com.explorer.gabom.domain.missionproof.controller;

import static com.explorer.gabom.domain.place.entity.QPlace.*;
import static org.springframework.data.domain.Sort.Direction.*;

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
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.missionproof.dto.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.domain.missionproof.service.MissionProofService;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.OffsetResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission-proof")
public class MissionProofController {

	private final MissionProofService missionProofService;

	// 미션 인증글 생성
	@PostMapping
	public ResponseEntity<ApiResponse<CreateMissionProofResponse>> createMissionProof(
		@Valid @RequestBody CreateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CreateMissionProofResponse response = missionProofService.createMissionProof(request, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("미션 인증이 완료되었습니다.", response));
	}

	// 미션 인증글 수정
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CreateMissionProofResponse>> updateMissionProof(
		@PathVariable Long id,
		@RequestBody UpdateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CreateMissionProofResponse response = missionProofService.updateMissionProof(id, request, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("수정 성공", response));
	}

	// 미션 인증글 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteMissionProof(
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		missionProofService.deleteMissionProof(id, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
	}

	// 미션 인증글 상세 조회
	@GetMapping("/{missionProofId}")
	public ResponseEntity<ApiResponse<MissionProofDetailResponse>> getMissionProofDetail(
		@PathVariable Long missionProofId
	) {
		MissionProofDetailResponse response = missionProofService.getMissionProofDetail(missionProofId);
		return ResponseEntity.ok(ApiResponse.success("조회 성공", response));
	}

	// 미션 인증글 리스트 조회
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<MissionProofSummary>>> getMissionProofs(
		@Valid ListMissionProofRequest request,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<MissionProofSummary> response = missionProofService.getMissionProofs(request, pageable);
		return ResponseEntity.ok(ApiResponse.success("미션 인증글 리스트 조회 성공", response));
	}
}
