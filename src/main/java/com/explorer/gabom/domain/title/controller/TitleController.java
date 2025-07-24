package com.explorer.gabom.domain.title.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TitleController {
	private final TitleService titleService;

	@PostMapping("/admin/titles")
	// @PreAuthorize("hasRole('ADMIN')") 어차피 동작 안해서 spring security 설정 적용 여부에 따라 수정 예정
	public ResponseEntity<ApiResponse<TitleCreateResponse>> createTitle(
		@RequestBody @Valid TitleCreateRequest request
	) {
		TitleCreateResponse response = titleService.createTitle(request);
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("칭호가 성공적으로 등록되었습니다.", response));
	}
}
