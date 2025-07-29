package com.explorer.gabom.domain.ranking.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.ranking.dto.response.RankingPage;
import com.explorer.gabom.domain.ranking.service.RankingService;
import com.explorer.gabom.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

	private final RankingService rankingService;

	@GetMapping
	public ResponseEntity<ApiResponse<RankingPage>> getRankingPage(
		@PageableDefault(page = 0, size = 10, sort = "level", direction = Sort.Direction.DESC) Pageable pageable
	) {
		RankingPage response = rankingService.getRankingPage(pageable);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("전체 랭킹 조회를 완료하였습니다.", response));
	}
}
