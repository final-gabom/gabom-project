package com.explorer.gabom.domain.place.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(
	name = "PlaceAPI",
	description = "탐험 장소 등록, 수정, 삭제, 조회 등 장소(Place) 관련 기능을 제공합니다."
)
public interface PlaceControllerDocs {

	@Operation(
		summary = "장소 등록",
		description = "새로운 탐험 장소를 등록합니다.  \n"
			+ "- 장소 제목, 주소, 위도/경도, 인증 방법 등을 포함하여 장소 정보를 저장합니다.  \n"
			+ "- 요청이 성공하면 HTTP 201 상태 코드를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "장소 등록 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값")
	})
	ResponseEntity<?> createPlace(
		@Parameter(description = "등록할 장소 정보", required = true)
		@RequestBody @Valid PlaceCreateRequest request,

		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "장소 목록 조회",
		description = "지정된 좌표(위도, 경도)를 기준으로 탐험 가능한 장소 목록을 조회합니다.  \n"
			+ "- 키워드를 포함한 검색이 가능합니다.  \n"
			+ "- 거리순 정렬 및 페이지네이션이 적용됩니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장소 목록 조회 성공")
	})
	ResponseEntity<?> getPlaceList(
		@Parameter(description = "현재 위치의 위도", required = true)
		@RequestParam Double lat,

		@Parameter(description = "현재 위치의 경도", required = true)
		@RequestParam Double lng,

		@Parameter(description = "검색 키워드 (선택)", required = false)
		@RequestParam(required = false) String keyword,

		@Parameter(description = "페이지 정보 (기본값: 거리 기준 오름차순)", required = false)
		@PageableDefault(page = 0, size = 10, sort = "distance", direction = Sort.Direction.ASC) Pageable pageable
	);

	@Operation(
		summary = "장소 상세 조회",
		description = "특정 장소의 상세 정보를 조회합니다.  \n"
			+ "- 장소 ID를 기반으로 상세 정보를 응답합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장소 상세 조회 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장소 ID")
	})
	ResponseEntity<?> getPlaceDetail(
		@Parameter(description = "조회할 장소 ID", required = true)
		@PathVariable Long placeId
	);

	@Operation(
		summary = "장소 수정",
		description = "기존에 등록한 장소 정보를 수정합니다.  \n"
			+ "- 본인이 등록한 장소만 수정할 수 있습니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장소 수정 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
		@ApiResponse(responseCode = "403", description = "수정 권한 없음"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장소 ID")
	})
	ResponseEntity<?> updatePlace(
		@Parameter(description = "수정할 장소 ID", required = true)
		@PathVariable Long placeId,

		@Parameter(description = "수정할 장소 정보", required = true)
		@RequestBody PlaceUpdateRequest request,

		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "장소 삭제",
		description = "등록된 장소를 삭제합니다.  \n"
			+ "- 소프트 삭제 방식으로 실제 데이터는 제거되지 않으며, 상태 값이 변경됩니다.  \n"
			+ "- 본인이 등록한 장소만 삭제할 수 있습니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장소 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장소 ID")
	})
	ResponseEntity<?> deletePlace(
		@Parameter(description = "삭제할 장소 ID", required = true)
		@PathVariable Long placeId,

		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails
	);
}