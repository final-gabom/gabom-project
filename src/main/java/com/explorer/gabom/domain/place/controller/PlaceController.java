package com.explorer.gabom.domain.place.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceDetailResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceListResponse;
import com.explorer.gabom.domain.place.service.PlaceService;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

	private final PlaceService placeService;

	// 탐험 장소 생성
	@PostMapping
	public ResponseEntity<ApiResponse<PlaceCreateResponse>> createPlace(
		@RequestBody @Valid PlaceCreateRequest request
		/* TODO : 인증 로직 들어오면 주석해제
		@AuthenticationPrincipal User user */
	) {
		Long userId = 1L; // TODO: 인증 붙으면 교체
		PlaceCreateResponse response = placeService.createPlace(request, userId);

		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("장소 등록이 완료되었습니다.", response));
	}

	// 탐험 장소 리스트 조회(검색)
	/*
	TODO : -> 위도 경도 값 합쳐서 거리 값 추출 후 추가해야 함
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<Page<PlaceListResponse>>> getPlaceList(
		@RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal User user) {
		Page<PlaceListResponse> result = placeService.getPlaceList(query, page, size);
		return ResponseEntity.ok(ApiResponse.success("장소 조회에 성공했습니다.", result));
	}
	// 탐험 장소 상세 조회
	@GetMapping("/{placeId}")
	public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
		@PathVariable Long placeId, @AuthenticationPrincipal User user
	) {
		PlaceDetailResponse response = placeService.getPlaceDetail(placeId);
		return ResponseEntity.ok(ApiResponse.success("장소 조회에 성공", response));
	}

	// 탐험 장소 수정
	@PatchMapping("/{placeId}")
	public ResponseEntity<ApiResponse<Void>> updatePlace(
		@PathVariable Long placeId,
		@RequestBody PlaceUpdateRequest request
		/* TODO : 인증 로직 들어오면 주석해제
		@AuthenticationPrincipal User user */
	) {
		Long userId = 1L; // TODO: 인증 붙으면 교체
		placeService.updatePlace(placeId, request, userId);
		return ResponseEntity.ok(ApiResponse.success("장소가 수정되었습니다."));
	}

	// 탐험 장소 삭제
}
