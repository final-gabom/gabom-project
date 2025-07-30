package com.explorer.gabom.domain.ranking.service;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.global.dto.PageResponse;

public interface RankingService {
	PageResponse<RankingDto> getRankingPage(Pageable pageable);
}
