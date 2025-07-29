package com.explorer.gabom.domain.ranking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.domain.ranking.dto.response.RankingPage;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final RankingRepository rankingRepository;

	@Override
	public RankingPage getRankingPage(Pageable pageable) {
		Page<RankingDto> rankingDtoPage = rankingRepository.findAll(pageable)
														   .map(RankingDto::toDto);
		return RankingPage.toDto(rankingDtoPage);
	}
}
