package com.explorer.gabom.domain.ranking.service;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.ranking.dto.response.RankingPage;

public interface RankingService {
	RankingPage getRankingPage(Pageable pageable);
}
