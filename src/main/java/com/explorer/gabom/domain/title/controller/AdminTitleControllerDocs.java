package com.explorer.gabom.domain.title.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "AdminTitleAPI",
	description = "칭호 등록, 수정, 삭제 등 관리자의 칭호(Title) 관련 기능을 제공합니다."
)
public interface AdminTitleControllerDocs {

	@Operation(summary = "칭호 등록",
		description = "새로운 칭호를 등록합니다. \n"
			+ "- 요청이 성공하면 HTTP 201 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "칭호 등록 성공"),
		@ApiResponse(responseCode = "400", description = "중복된 칭호 이름으로 등록 시도"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "권한이 없는 사용자가 등록 시도"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> createTitle(
		@RequestBody TitleCreateRequest request);

	@Operation(summary = "칭호 수정",
		description = "기존 칭호를 수정합니다. \n"
			+ "- 요청이 성공하면 HTTP 200 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "칭호 수정 성공"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "권한이 없는 사용자가 등록 시도"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 칭호 수정 시도"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@Parameter(
		name = "titleId",
		description = "수정할 칭호 ID",
		required = true)
	ResponseEntity<?> updateTitle(
		@PathVariable Long titleId,
		@RequestBody TitleUpdateRequest request);

	@Operation(summary = "칭호 삭제",
		description = "기존 칭호를 삭제합니다. \n"
			+ "- 요청이 성공하면 HTTP 200 상태 코드를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "칭호 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "권한이 없는 사용자가 등록 시도"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 칭호 수정 시도"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@Parameter(
		name = "titleId",
		description = "삭제할 칭호 ID",
		required = true)
	ResponseEntity<?> deleteTitle(
		@PathVariable Long titleId);
}
