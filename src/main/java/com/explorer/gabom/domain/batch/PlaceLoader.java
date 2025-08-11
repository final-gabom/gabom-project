package com.explorer.gabom.domain.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.batch.service.PlaceImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(3) // 여러 CommandLineRunner 중 실행 순서. 숫자 낮은 것 → 먼저 실행
@Component
@RequiredArgsConstructor
public class PlaceLoader implements CommandLineRunner {

	// CSV → DB 임포트 핵심 로직을 가진 서비스
	private final PlaceImportService service;

	// application-*.yml 에서 place.import.enabled=true 로 켜짐 (기본 false)
	@Value("${place.import.enabled:false}")
	private boolean enabled;

	@Override
	public void run(String... args) {
		// 1) 기능 on/off 스위치: 프로퍼티가 false면 바로 종료
		if (!enabled) {
			log.info("[PlaceLoader] 비활성화됨. 스킵");
			return;
		}
		try {
			// 2) 실제 임포트 시작
			log.info("[PlaceLoader] 시작 (CSV → DB)");
			service.loadFromClasspath();
			log.info("[PlaceLoader] 완료.");
		} catch (Exception e) {
			// 3) 배치 실패 시 전체 애플리케이션 부팅을 멈출지 여부
			//    - 현재는 IllegalStateException으로 래핑 후 throw → 부팅 실패
			//    - 개발 중엔 이게 편하고, 운영에선 로깅만 하고 계속 부팅하도록 바꾸기도 함
			log.error("[PlaceLoader] 실패: {}", e.getMessage(), e);
			throw new IllegalStateException("Place 초기 적재 실패", e);
		}
	}
}



