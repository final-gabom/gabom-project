package com.explorer.gabom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 배치 전용 스레드풀 설정.
 *
 * 설계 포인트
 * - Executor 타입 Bean을 @Primary로 등록해 @Qualifier 없이도 주입 간소화.
 * - 코어/최대 풀 사이즈, 큐 용량, 스레드 네이밍, 그레이스풀 셧다운 설정.
 * - PlaceImportService에서는 ExecutorCompletionService에 "그대로" 주입하여 사용.
 */
@Configuration
public class BatchConfig {

	@Bean(name = "placeImportExecutor")
	@Primary // 같은 타입의 다른 Executor Bean이 있어도 기본 선택이 되도록 지정
	public Executor placeImportExecutor() {
		ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();

		// CPU 개수 기반 기본값 (환경/DB 여력에 따라 조절)
		int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
		ex.setCorePoolSize(cores);          // 상시 유지 스레드 수
		ex.setMaxPoolSize(cores * 2);       // 최대 스레드 수(폭주 시 확장)
		ex.setQueueCapacity(1000);          // 대기 큐 용량(너무 크면 메모리 압박)
		ex.setThreadNamePrefix("place-import-");

		// 애플리케이션 종료 시, 큐에 남은 작업이 끝나길 기다림(그레이스풀 셧다운)
		ex.setWaitForTasksToCompleteOnShutdown(true);
		ex.setAwaitTerminationSeconds(60);

		ex.initialize();
		return ex; // 반환 타입은 Executor → 서비스에서 단순 주입 가능
	}
}
