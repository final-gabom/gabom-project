package com.explorer.gabom.domain.ranking.scheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingScheduler {

	private final RankingRepository rankingRepository;

	@Scheduled(fixedRate = 60000)
	@Transactional
	public void updateRankings() {
		List<Ranking> rankingList = rankingRepository.findAllByOrderByExpDescIdAsc();
		AtomicInteger counter = new AtomicInteger(1);
		rankingList.forEach(r -> r.updateRankNo(counter.getAndIncrement()));
		rankingRepository.saveAll(rankingList);
	}
}
