package com.explorer.gabom.domain.title.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.dto.response.TitleUpdateResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/titles")
@RequiredArgsConstructor
public class AdminTitleController {
	private final TitleService titleService;

	@PostMapping
	public ResponseEntity<ApiResponse<TitleCreateResponse>> createTitle(
		@RequestBody @Valid TitleCreateRequest request
	) {
		TitleCreateResponse response = titleService.createTitle(request);
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("칭호가 성공적으로 등록되었습니다.", response));
	}

	@PatchMapping("/{titleId}")
	public ResponseEntity<ApiResponse<TitleUpdateResponse>> updateTitle(
		@PathVariable Long titleId,
		@RequestBody @Valid TitleUpdateRequest request) {

		TitleUpdateResponse updated = titleService.updateTitle(titleId, request);
		return ResponseEntity.ok(ApiResponse.success("칭호가 성공적으로 수정되었습니다.", updated));
	}

	@DeleteMapping("/{titleId}")
	public ResponseEntity<ApiResponse<TitleDeleteResponse>> deleteTitle(
		@PathVariable Long titleId) {

		TitleDeleteResponse deleted = titleService.deleteTitle(titleId);
		return ResponseEntity.ok(ApiResponse.success("칭호가 성공적으로 삭제되었습니다.", deleted));
	}
}
