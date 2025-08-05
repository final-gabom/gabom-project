package com.explorer.gabom.domain.title.controller;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserTitleAPI Document", description = "칭호 관련 유저 기능 API 문서화")
public interface UserTitleControllerDocs {

	@Operation(summary = "칭호 조회")
	@ApiResponse(responseCode = "201", description = "칭호가 성공적으로 조회되었습니다.")
	@Parameter(name = "userId", description = "조회할 유저 ID")
	ResponseEntity<?> getUserTitles(Long userId);
}
