package com.explorer.gabom.domain.ranking.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.explorer.gabom.domain.ranking.repository.RankingRepository;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

	@InjectMocks
	private RankingServiceImpl rankingService;

	@Mock
	private RankingRepository rankingRepository;

}
