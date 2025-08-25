package com.explorer.gabom.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.explorer.gabom.domain.elasticsearch.indexer.PlaceSearchIndexer;

@Configuration
@RequiredArgsConstructor
public class StartupTask {

	private final PlaceSearchIndexer indexer;

	@Bean
	CommandLineRunner runIndexerOnStartup() {
		return args -> {
			indexer.reindexAll(); // 앱 시작 시 실행
		};
	}
}

