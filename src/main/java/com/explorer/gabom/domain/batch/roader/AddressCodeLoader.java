package com.explorer.gabom.domain.batch.roader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.batch.service.AddressCodeLoaderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class AddressCodeLoader implements CommandLineRunner {

	private final AddressCodeLoaderService service;

	@Value("${address.import.enabled:false}")
	private boolean enabled;

	@Value("${address.import.path:}")
	private String addressCsvPath;

	// 컨트롤러에서 호출할 메서드
	public void loadAll() throws Exception {
		log.info("[AddressCodeLoader] 시작 (CSV -> DB) {}", addressCsvPath == null || addressCsvPath.isBlank() ? "" : "path=" + addressCsvPath);
		if (addressCsvPath == null || addressCsvPath.isBlank()) {
			service.loadFromClasspath();			// 기존 무인자 메서드 시용
		}
		log.info("[AddressCodeLoader] 완료");
	}

	@Override
	public void run(String... args) {
		if (!enabled) {
			log.info("[AddressCodeLoader] 비활성화됨. 스킵");
			return;
		}
		try {
			loadAll();
		} catch (Exception e) {
			log.error("[AddressCodeLoader] 실패: {}", e.getMessage(), e);
			throw new IllegalStateException("법정동 코드 초기화 실패", e);
		}
	}
}
