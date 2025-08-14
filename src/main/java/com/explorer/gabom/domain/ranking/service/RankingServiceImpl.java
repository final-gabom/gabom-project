package com.explorer.gabom.domain.ranking.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.ranking.dto.RankingDto;
import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;
import com.explorer.gabom.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final RankingRepository rankingRepository;

	@Override
	public PageResponse<RankingDto> getRankingPage(Pageable pageable) {
		Page<Ranking> rankingPage = rankingRepository.findAllByOrderByExpDescIdAsc(pageable);

		int startRank = (int)pageable.getOffset() + 1;
		AtomicInteger rankCounter = new AtomicInteger(startRank);

		List<RankingDto> rankingDtoList = rankingPage.getContent().stream()
													 .map(r -> RankingDto.toDto(r, rankCounter.getAndIncrement()))
													 .toList();

		return PageResponse.toDto(new PageImpl<>(rankingDtoList, pageable, rankingPage.getTotalElements()));
	}
}
