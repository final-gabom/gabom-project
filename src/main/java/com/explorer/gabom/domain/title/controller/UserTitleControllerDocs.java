package com.explorer.gabom.domain.title.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "UserTitleAPI",
	description = "칭호 조회 등 유저의 칭호(Title) 관련 기능을 제공합니다."
)
public interface UserTitleControllerDocs {

	@Operation(summary = "칭호 조회",
		description = "유저의 칭호를 조회합니다. \n"
			+ "- 요청이 성공하면 HTTP 200 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "칭호 조회 성공"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 만료"),
		@ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@Parameter(
		name = "userId",
		description = "조회할 유저 ID",
		required = true)
	ResponseEntity<?> getUserTitles(
		@PathVariable Long userId);
}
