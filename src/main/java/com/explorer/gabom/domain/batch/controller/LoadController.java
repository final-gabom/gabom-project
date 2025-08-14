package com.explorer.gabom.domain.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.explorer.gabom.domain.batch.roader.AddressCodeLoader;
import com.explorer.gabom.domain.batch.roader.PlaceLoader;
import com.explorer.gabom.domain.batch.roader.SeoulPlaceLoader;

@Slf4j
@RestController
@RequestMapping("/admin/load")
@RequiredArgsConstructor
public class LoadController {

	private final AddressCodeLoader addressCodeLoader;
	private final SeoulPlaceLoader seoulPlaceLoader;
	private final PlaceLoader placeLoader;

	/** 1) 주소 로더 실행 */
	@PostMapping("/address")
	public ResponseEntity<LoadResponse> loadAddressCode() {
		long t0 = System.currentTimeMillis();
		try {
			addressCodeLoader.loadAll(); // 동기 실행
			return ResponseEntity.ok(LoadResponse.ok("/admin/load/address", t0, "address load 성공"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(LoadResponse.fail("/admin/load/address", t0, e));
		}
	}

	/** 2) 서울 플레이스 로더 실행 */
	@PostMapping("/place/seoul")
	public ResponseEntity<LoadResponse> loadPlaceSeoul() {
		long t0 = System.currentTimeMillis();
		try {
			log.info("[/place/seoul] start");
			int saved = seoulPlaceLoader.loadAll(); // 동기 실행
			log.info("[/place/seoul] end saved={}", saved);
			return ResponseEntity.ok(LoadResponse.ok("/admin/load/place/seoul", t0, "saved=" + saved));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(LoadResponse.fail("/admin/load/place/seoul", t0, e));
		}
	}

	/** 3) 전체 플레이스 로더 실행 */
	@PostMapping("/place")
	public ResponseEntity<LoadResponse> loadPlace() {
		long t0 = System.currentTimeMillis();
		try {
			int saved = placeLoader.loadAll(); // 동기 실행
			return ResponseEntity.ok(LoadResponse.ok("/admin/load/place", t0, "saved=" + saved));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(LoadResponse.fail("/admin/load/place", t0, e));
		}
	}

	/** 간단 응답 DTO */
	public record LoadResponse(
		boolean success,
		String endpoint,
		String message,
		long elapsedMs
	) {
		static LoadResponse ok(String endpoint, long startedMs, String msg) {
			return new LoadResponse(true, endpoint, msg, System.currentTimeMillis() - startedMs);
		}
		static LoadResponse fail(String endpoint, long startedMs, Exception e) {
			String msg = (e.getMessage() == null || e.getMessage().isBlank())
						 ? e.getClass().getSimpleName()
						 : e.getMessage();
			return new LoadResponse(false, endpoint, "failed: " + msg, System.currentTimeMillis() - startedMs);
		}
	}
}

