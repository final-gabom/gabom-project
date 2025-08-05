package com.explorer.gabom.domain.activity.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "UserActivityLogAPI",
	description = "활동로그 조회 등 유저의 활동로그(ActivityLog) 관련 기능을 제공합니다."
)
public interface UserActivityLogControllerDocs {

	@Operation(
		summary     = "활동로그 조회",
		description = "유저의 활동로그를 조회합니다.  \n"
			+ "- 요청이 성공하면 HTTP 200 상태 코드를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "활동로그 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 파라미터 값"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 만료"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@Parameters({
		@Parameter(name = "page", description = "조회할 페이지 번호 (0부터 시작)"),
		@Parameter(name = "size", description = "한 페이지에 표시할 데이터 개수"),
		@Parameter(name = "sort", description = "정렬 기준 (ex: createdAt,desc)"),
		@Parameter(name = "from", description = "조회 시작일시 (ex: 2025-08-01T00:00:00)"),
		@Parameter(name = "to", description = "조회 종료일시 (ex: 2025-08-02T23:59:59)")
	})
	ResponseEntity<com.explorer.gabom.global.dto.ApiResponse<PageResponse<UserActivityLogResponse>>> getMyLogs(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
		@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	);
}
