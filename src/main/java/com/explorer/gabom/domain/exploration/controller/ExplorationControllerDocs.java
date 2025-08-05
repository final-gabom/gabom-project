package com.explorer.gabom.domain.exploration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "ExplorationAPI",
	description = "탐험 시작, 제한 시간 연장, 진행 중인 장소 조회 등 탐험(Exploration) 관련 기능을 제공합니다."
)
public interface ExplorationControllerDocs {

	@Operation(
		summary     = "탐험 시작",
		description = "지정된 장소에서 새로운 탐험을 시작합니다.  \n"
			+ "- 장소 ID를 기반으로 탐험을 생성하며,  \n"
			+ "- 중복된 탐험이 존재할 경우 예외가 발생합니다.  \n"
			+ "- 요청이 성공하면 HTTP 201 상태 코드를 반환합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "탐험 시작 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 장소 ID"),
		@ApiResponse(responseCode = "409", description = "이미 탐험 중인 상태")
	})
	ResponseEntity<?> startExploration(
		@Parameter(description = "탐험을 시작할 장소 ID", required = true)
		@PathVariable Long placeId,

		@Parameter(description = "탐험 시작 요청 정보", required = true)
		@RequestBody ExplorationStartRequest request,

		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary     = "진행 중인 탐험 조회",
		description = "현재 로그인한 사용자의 진행 중인 탐험 정보를 조회합니다.  \n"
			+ "- 탐험 중이 아닐 경우 404 에러를 반환합니다.  \n"
			+ "- 탐험 정보와 남은 시간을 포함해 응답합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "탐험 정보 조회 성공"),
		@ApiResponse(responseCode = "404", description = "진행 중인 탐험 없음")
	})
	ResponseEntity<?> getCurrentExploration(
		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary     = "탐험 제한 시간 연장",
		description = "지정된 탐험의 제한 시간을 3시간 연장합니다.  \n"
			+ "- 연장은 한 번만 가능하며,  \n"
			+ "- 이미 연장된 탐험일 경우 예외가 발생합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "제한 시간 연장 성공"),
		@ApiResponse(responseCode = "400", description = "이미 연장된 탐험"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 탐험 ID"),
		@ApiResponse(responseCode = "403", description = "본인의 탐험이 아님")
	})
	ResponseEntity<?> extendExplorationTime(
		@Parameter(hidden = true)
		@AuthenticationPrincipal CustomUserDetails userDetails,

		@Parameter(description = "제한 시간을 연장할 탐험 ID", required = true)
		@PathVariable Long explorationId
	);
}