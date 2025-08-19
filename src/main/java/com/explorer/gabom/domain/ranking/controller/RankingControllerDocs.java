package com.explorer.gabom.domain.ranking.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
			+ "- 경험치(exp) 기준으로 내림차순 정렬된 랭킹을 제공합니다. \n"
			+ "- nickname 파라미터를 입력하면 해당 nickname을 포함한 유저만 필터링하여 조회합니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetRankingPage"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "랭킹 조회에 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "404", description = "랭킹 데이터 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> getRankingPage(
		@Parameter(
			description = "조회할 유저 nickname (선택, 필터링용)",
			required = false,
			in = ParameterIn.QUERY
		)
		@RequestParam(value = "nickname", required = false) String nickname,

		@Parameter(
			description = "페이징 정보 (기본: page=0, size=10, sort=exp,DESC)",
			required = false
		)
		@PageableDefault(page = 0, size = 10, sort = "exp", direction = Sort.Direction.DESC)
		Pageable pageable
	);

	@Operation(
		summary = "개별 유저 랭킹 조회",
		description = "특정 유저의 랭킹 정보를 조회합니다. \n"
			+ "- userId를 입력하지 않으면 로그인한 유저 기준으로 조회합니다. \n"
			+ "- 성공 시 200 OK 상태 코드를 반환합니다.",
		tags = {"GetRanking"}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "랭킹 조회에 성공"),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더(JWT 토큰)가 없거나 만료"),
		@ApiResponse(responseCode = "404", description = "랭킹 데이터 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<?> getRanking(
		@Parameter(
			description = "조회할 유저 ID (선택, 입력 없으면 로그인 유저 기준)",
			required = false,
			in = ParameterIn.QUERY
		)
		@RequestParam(value = "userId", required = false) Long userId,

		@Parameter(hidden = true)
		CustomUserDetails userDetails
	);
}
