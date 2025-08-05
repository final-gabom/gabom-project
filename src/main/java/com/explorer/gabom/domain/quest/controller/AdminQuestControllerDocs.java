package com.explorer.gabom.domain.quest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequest;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
	name = "AdminQuestAPI",
	description = "관리자 전용 퀘스트 생성, 수정, 삭제 관련 기능을 제공합니다."
)
public interface AdminQuestControllerDocs {

	@Operation(
		summary = "퀘스트 생성",
		description = "관리자가 새로운 퀘스트를 생성합니다. \n"
			+ "- 보상 칭호 ID가 존재해야 하며, \n"
			+ "- 인증 및 관리자 권한이 필요합니다. \n"
			+ "- 성공 시 요청 정보에 따라 퀘스를 생성하고 201 Created 상태 코드를 반환합니다.",
		tags = {"CreateQuest"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "퀘스트 등록에 성공"),
		@ApiResponse(responseCode = "400", description = "INVALID_TITLE_ID: 존재하지 않는 보상 칭호 ID"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> createQuest(
		@Parameter(description = "퀘스트 생성 요청 정보", required = true)
		@RequestBody @Valid QuestCreateRequest request
	);

	@Operation(
		summary = "퀘스트 수정",
		description = "관리자가 기존 퀘스트 정보를 수정합니다. \n"
			+ "- 수정할 필드를 선택적으로 포함할 수 있으며, \n"
			+ "- 수정할 퀘스트가 존재하지 않으면 404 상태 코드를 반환합니다. \n"
			+ "- 성공 시 해당 퀘스트의 정보를 요청 정보에 따라 수정하고 200 OK 상태 코드를 반환합니다.",
		tags = {"UpdateQuest"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "퀘스트 수정에 성공"),
		@ApiResponse(responseCode = "400", description = "데이터 타입이 올바르지 않음"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자"),
		@ApiResponse(responseCode = "404", description = "QUEST_NOT_FOUND: 해당 퀘스트가 존재하지 않음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> updateQuest(
		@Parameter(description = "수정할 퀘스트 ID", required = true)
		@PathVariable Long questId,
		@Parameter(description = "퀘스트 수정 요청 정보", required = true)
		@RequestBody @Valid QuestUpdateRequest request
	);

	@Operation(
		summary = "퀘스트 삭제",
		description = "관리자가 퀘스트를 삭제합니다. \n"
			+ "- 삭제할 퀘스트가 존재하지 않으면 404 상태 코드를 반환합니다. \n"
			+ "- 성공 시 해당 퀘스트를 소프트 딜리트 처리하고 200 OK 상태 코드를 반환합니다.",
		tags = {"DeleteQuest"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "퀘스트 삭제에 성공"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자"),
		@ApiResponse(responseCode = "404", description = "QUEST_NOT_FOUND: 해당 퀘스트가 존재하지 않음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> deleteQuest(
		@Parameter(description = "삭제할 퀘스트 ID", required = true)
		@PathVariable Long questId
	);
}
