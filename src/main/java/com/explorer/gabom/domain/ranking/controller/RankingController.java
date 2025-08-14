package com.explorer.gabom.domain.ranking.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.domain.ranking.service.RankingService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController implements RankingControllerDocs {

	private final RankingService rankingService;

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<RankingDto>>> getRankingPage(
		@PageableDefault(page = 0, size = 10, sort = "exp", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<RankingDto> response = rankingService.getRankingPage(pageable);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("전체 랭킹 조회를 완료하였습니다.", response));
	}
}
