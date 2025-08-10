package com.explorer.gabom.domain.batch;

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

	@Override
	public void run(String... args) {
		if (!enabled) {
			log.info("[AddressCodeLoader] 비활성화됨. 스킵");
			return;
		}

		try {
			log.info("[AddressCodeLoader] 시작 (CSV → DB)");
			service.loadFromClasspath();
			log.info("[AddressCodeLoader] 완료");
		} catch (Exception e) {
			log.error("[AddressCodeLoader] 실패: {}", e.getMessage(), e);
			throw new IllegalStateException("법정동 코드 초기화 실패", e);
		}
	}
}
