package com.explorer.gabom.domain.title.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/titles")
@RequiredArgsConstructor
public class TitleController {
	private final TitleService titleService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<TitleResponse>> createTitle(
		@RequestBody @Valid TitleCreateRequest request
		) {
		TitleResponse response = titleService.createTitle(request);
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("칭호가 성공적으로 등록되었습니다.", response));
	}
}
