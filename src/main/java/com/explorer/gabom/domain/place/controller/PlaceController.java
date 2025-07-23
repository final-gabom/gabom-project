package com.explorer.gabom.domain.place.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;

	// 탐험 장소 생성
	@PostMapping
	public ResponseEntity<Void> createPlace(
		@RequestBody PlaceCreateRequest request /* TODO: , @AuthenticationPrincipal User user */) {
		// TODO: 실제 유저 정보는 인증 후 받아 처리 예정
		placeService.createPlace(request /* , user */);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
