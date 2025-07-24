package com.explorer.gabom.domain.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.service.PlaceService;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;

	// 탐험 장소 생성
	@PostMapping
	public ResponseEntity<ApiResponse<PlaceCreateResponse>> createPlace(
		@RequestBody PlaceCreateRequest request,
		@AuthenticationPrincipal User user
	) {
		PlaceCreateResponse response = placeService.createPlace(request, user);
		return ResponseEntity.ok(ApiResponse.success("장소 등록이 완료되었습니다.", response));
	}
}
