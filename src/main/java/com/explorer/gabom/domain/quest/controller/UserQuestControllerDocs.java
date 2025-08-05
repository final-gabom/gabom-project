package com.explorer.gabom.domain.quest.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "UserQuestAPI",
	description = "유저-퀘스트 진행 조회 및 보상 수령 관련 기능을 제공합니다."
)
public interface UserQuestControllerDocs {

	@Operation(
		summary = "유저 퀘스트 진행 상태 조회",
		description = "로그인한 사용자의 퀘스트 진행 상태를 조회합니다. \n"
			+ "- 인증이 필요한 요청입니다. \n"
			+ "- status 파라미터로 NOT_STARTED, IN_PROGRESS, COMPLETED 중 선택적으로 필터링할 수 있습니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetUserQuestProgress"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "유저-퀘스트 진행 상태 조회에 성공"),
		@ApiResponse(responseCode = "400", description = "RequestParam status의 값이 ProgressStatus Enum에 없는 경우"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> getProgress(
		@Parameter(hidden = true) CustomUserDetails userDetails,

		@Parameter(description = "페이징 정보 (기본: page=0, size=10, sort=id,ASC)", required = false)
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,

		@Parameter(
			name = "status",
			description = "퀘스트 진행 상태 (NOT_STARTED, IN_PROGRESS, COMPLETED)",
			required = false,
			in = ParameterIn.QUERY
		)
		@RequestParam(value = "status", required = false) ProgressStatus progressStatus

	);

	@Operation(
		summary = "퀘스트 보상 수령",
		description = "완료된 퀘스트에 대해 보상을 수령합니다. \n"
			+ "- 인증이 필요한 요청입니다. \n"
			+ "- 퀘스트가 완료된 상태이며, 보상을 받지 않은 상태여야 합니다. \n"
			+ "- 이미 보상을 받았거나 완료되지 않은 퀘스트인 경우 400 상태 코드를 반환합니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"ClaimQuestReward"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "퀘스트 보상 수령에 성공한 경우"),
		@ApiResponse(responseCode = "400", description = "REWARD_ALREADY_CLAIMED: 이미 보상 받음 / NOT_COMPLETED: 완료되지 않은 퀘스트"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "404", description = "QUEST_NOT_FOUND: 퀘스트를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> claimReward(
		@Parameter(hidden = true) CustomUserDetails userDetails,

		@Parameter(description = "보상을 수령할 유저 퀘스트 ID", required = true)
		@PathVariable Long userQuestId
	);
}
