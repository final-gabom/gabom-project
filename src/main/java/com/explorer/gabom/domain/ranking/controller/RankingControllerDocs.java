package com.explorer.gabom.domain.ranking.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	name = "RankingAPI",
	description = "랭킹 조회 기능을 제공합니다."
)
public interface RankingControllerDocs {

	@Operation(
		summary = "전체 랭킹 조회",
		description = "유저 랭킹을 페이지네이션 방식으로 조회합니다. \n"
			+ "- 경험치(exp) 기준으로 정렬된 랭킹 페이지를 조회합니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetRankingPage"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "랭킹 조회에 성공"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@GetMapping
	ResponseEntity<?> getRankingPage(
		@Parameter(description = "페이징 정보 (기본: page=0, size=10, sort=exp,DESC)", required = false)
		@PageableDefault(page = 0, size = 10, sort = "exp", direction = Sort.Direction.DESC) Pageable pageable
	);
}
