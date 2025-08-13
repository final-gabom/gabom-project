package com.explorer.gabom.domain.batch.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.batch.util.CsvImporter;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceImportService {

	// CSV 읽기 전용 컴포넌트
	private final CsvImporter csvImporter;
	// 관리자 유저 조회용
	private final UserRepository userRepository;
	// 실제 한 행(Place+Address) 저장 로직(행 단위 트랜잭션)을 가진 컴포넌트
	private final PlaceRowImporter rowImporter;

	/**
	 * 리소스(classpath)의 CSV 파일을 읽어서 일괄 임포트한다.
	 * - 관리자 계정(admin@test)을 한 번 조회해서 모든 Place.user로 사용
	 * - CSV 각 행은 PlaceRowImporter.importOne(...)에서
	 *   REQUIRES_NEW 트랜잭션으로 개별 처리 → 한 행 실패해도 다음 행 계속
	 * - 처리 성공 건수(saved)를 리턴
	 */
	public int loadFromClasspath(String url) throws Exception {
		// 1) 관리자 유저 미리 확보 (없으면 시더 먼저 실행)
		User admin = userRepository.findByEmail("admin@test")
								   .orElseThrow(() -> new IllegalStateException("admin@test 없음(시더 먼저 실행 필요)"));

		// 2) CSV 파싱 (List<PlaceCsvRow>)
		var rows = csvImporter.readPlaceCsv(url);
		int saved = 0;

		// 3) 행 단위 처리 루프
		for (var row : rows) {
			// emdcd 없는 행 스킵(Blank 사용하여 공백 포함)
			if (row.getEmdCd() == null || row.getEmdCd().isBlank()) {
				log.warn("[PlaceImport] skip title='{}' reason=emdCd 누락", row.getTitle());
				continue;
			}
			try {
				// - 실제 저장은 행 전용 컴포넌트에서 수행 (트랜잭션 경계도 거기서 관리)
				rowImporter.importOne(row, admin);
				saved++;
			} catch (Exception e) {
				// - 특정 행만 스킵하고 계속 진행 (배치 멈추지 않도록)
				log.warn("[PlaceImport] skip title='{}': {}", row.getTitle(), e.getMessage());
			}
		}
		// 4) 총 성공 건수 반환
		return saved;
	}
}





