package com.explorer.gabom.domain.missionproof.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofDetailResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "MissionProof", description = "미션 인증글 등록, 수정, 삭제, 조회 기능을 제공합니다.")
public interface MissionProofControllerDocs {

	@Operation(summary = "미션 인증글 생성", description = "탐험 장소에 대한 미션 인증글을 작성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "404", description = "대상 장소 없음")
	})
	ResponseEntity<?> createMissionProof(
		@RequestBody @Valid CreateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(summary = "미션 인증글 수정", description = "작성한 인증글을 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "403", description = "작성자 불일치"),
		@ApiResponse(responseCode = "404", description = "해당 인증글 없음")
	})
	ResponseEntity<?> updateMissionProof(
		@PathVariable Long id,
		@RequestBody @Valid UpdateMissionProofRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(summary = "미션 인증글 삭제", description = "작성한 인증글을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "작성자 불일치"),
		@ApiResponse(responseCode = "404", description = "해당 인증글 없음")
	})
	ResponseEntity<?> deleteMissionProof(
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(summary = "미션 인증글 상세 조회", description = "해당 인증글의 전체 내용을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "해당 인증글 없음")
	})
	ResponseEntity<?> getMissionProofDetail(
		@PathVariable Long id
	);
}
