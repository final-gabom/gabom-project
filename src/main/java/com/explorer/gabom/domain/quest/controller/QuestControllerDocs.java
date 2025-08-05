package com.explorer.gabom.domain.quest.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "QuestAPI",
	description = "퀘스트 목록 및 단건 조회 관련 기능을 제공합니다."
)
public interface QuestControllerDocs {

	@Operation(
		summary = "퀘스트 전체 조회",
		description = "모든 퀘스트를 페이지네이션 방식으로 조회합니다. \n"
			+ "- 기본 정렬 기준은 생성일자(createdAt) 내림차순이며, \n"
			+ "- 인증이 필요한 요청입니다.\n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetQuestPage"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "퀘스트 조회에 성공"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@GetMapping
	ResponseEntity<?> getQuestPage(
		@Parameter(description = "페이징 정보 (기본: page=0, size=10, sort=createdAt,DESC)", required = false)
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	);

	@Operation(
		summary = "퀘스트 단건 조회",
		description = "지정한 ID의 퀘스트 정보를 조회합니다. \n"
			+ "- 인증이 필요한 요청입니다. \n"
			+ "- 해당 퀘스트가 존재하지 않으면 404 상태 코드를 반환합니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetQuest"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "퀘스트 조회에 성공"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "404", description = "QUEST_NOT_FOUND: 해당 퀘스트가 존재하지 않음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@GetMapping("/{questId}")
	ResponseEntity<?> getQuestById(
		@Parameter(description = "조회할 퀘스트 ID", required = true)
		@PathVariable Long questId
	);
}
