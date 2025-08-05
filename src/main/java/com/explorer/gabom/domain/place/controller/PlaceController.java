package com.explorer.gabom.domain.place.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.explorer.gabom.domain.place.dto.response.PlaceDetailResponseDto;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.service.PlaceService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController implements PlaceControllerDocs {

	private final PlaceService placeService;

	// 탐험 장소 생성
	@PostMapping
	public ResponseEntity<ApiResponse<PlaceCreateResponse>> createPlace(@RequestBody @Valid PlaceCreateRequest request,
																		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		PlaceCreateResponse response = placeService.createPlace(request, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("장소 등록이 완료되었습니다.", response));
	}

	// 탐험 장소 리스트 조회(검색)
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<PlaceSummary>>> getPlaceList(
		@RequestParam Double lat,
		@RequestParam Double lng,
		@RequestParam(required = false) String keyword,
		@PageableDefault(page = 0, size = 10, sort = "distance", direction = Sort.Direction.ASC) Pageable pageable) {
		PageResponse<PlaceSummary> result = placeService.getPlaceList(keyword, lat, lng, pageable);
		return ResponseEntity.ok(ApiResponse.success("장소 리스트 조회 성공", result));
	}

	// 탐험 장소 상세 조회
	@GetMapping("/{placeId}")
	public ResponseEntity<ApiResponse<PlaceDetailResponseDto>> getPlaceDetail(
		@PathVariable Long placeId
	) {
		PlaceDetailResponseDto response = placeService.getPlaceDetail(placeId);
		return ResponseEntity.ok(ApiResponse.success("탐험 장소 상세 조회 성공", response));
	}

	// 탐험 장소 수정
	@PatchMapping("/{placeId}")
	public ResponseEntity<ApiResponse<Void>> updatePlace(@PathVariable Long placeId,
														 @RequestBody PlaceUpdateRequest request,
														 @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		placeService.updatePlace(placeId, userId, request);
		return ResponseEntity.ok(ApiResponse.success("장소가 수정되었습니다."));
	}

	// 탐험 장소 삭제
	@DeleteMapping("/{placeId}")
	public ResponseEntity<ApiResponse<Void>> deletePlace(@PathVariable Long placeId,
														 @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		placeService.deletePlace(placeId, userId);
		return ResponseEntity.ok(ApiResponse.success("장소가 삭제되었습니다."));
	}
}
