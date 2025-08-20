package com.explorer.gabom.domain.batch.service;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.batch.util.CsvImporter;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CSV → DB 임포트를 수행하는 서비스.
 *
 * 설계 포인트
 * - 각 행(row)은 PlaceRowImporter.importOne(...)에서 REQUIRES_NEW 트랜잭션으로 독립 실행
 *   → 특정 행 실패가 전체 배치를 중단시키지 않음(부분 성공 허용).
 * - ExecutorCompletionService를 사용해 "끝난 작업부터" 회수
 *   → 몇 개의 느린 작업 때문에 전체 완료가 지연되는 것을 완화.
 * - Semaphore로 제출량(동시에 큐/풀에 떠 있는 작업 수)을 제한
 *   → 과도한 적체/메모리 사용/리젝션 방지(간단한 백프레셔).
 * - Executor(스레드풀)는 Spring Bean으로 주입받고, 여기서는 shutdown 하지 않음
 *   → 애플리케이션 전역에서 재사용, 수명 관리는 Spring이 담당.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceImportService {

	/** CSV 파일을 로드하고 한 줄을 DTO(PlaceCsv)로 변환하는 컴포넌트 */
	private final CsvImporter csvImporter;

	/** 관리자 계정(admin@test) 조회에 사용 */
	private final UserRepository userRepository;

	/** 한 행(Place + Address) 저장을 담당하는 컴포넌트. 내부에서 REQUIRES_NEW 트랜잭션 사용 */
	private final PlaceRowImporter rowImporter;

	/** 배치 전용 스레드풀(예: ThreadPoolTaskExecutor). BatchConfig에서 @Primary 등으로 정의 */
	private final Executor placeImportExecutor;

	/**
	 * 주어진 경로의 CSV(클래스패스/파일시스템)를 병렬로 임포트한다.
	 *
	 * 동작 요약:
	 * 1) 관리자 계정 한 번 조회
	 * 2) CSV 읽기
	 * 3) 각 행을 스레드풀에 제출 (emdCd 누락 시 스킵)
	 * 4) 완료된 작업부터 회수하며 성공 건수(saved) 집계
	 * 5) 처리량/소요시간 로그 출력
	 *
	 * @param url "classpath:data/xxx.csv" 또는 "file:/path/xxx.csv"
	 * @return 저장 성공 건수
	 * @throws Exception CSV 파싱/리소스 로딩 등에서 발생한 예외 전파
	 */
	public int loadFromClasspath(String url) throws Exception {
		// 1) 관리자 유저 확보(없으면 시더가 먼저 실행되어야 함)
		final User admin = userRepository.findByEmail("admin@test")
										 .orElseThrow(() -> new IllegalStateException("admin@test 없음(시더 먼저 실행 필요)"));

		// 2) CSV 파싱 (List<PlaceCsv>)
		final var rows = csvImporter.readPlaceCsv(url);
		final int total = rows.size();              // 전체 행 수
		final long startNs = System.nanoTime();     // 처리 시작 시각(성능 측정용)

		// 3) 간단한 백프레셔 설정
		//    - 동시에 큐/풀에 떠 있을 수 있는 작업 수 상한을 cores*4로 제한
		//    - 과도한 제출로 인한 메모리/리젝션 문제 완화
		final int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
		final int maxInFlight = cores * 4;
		final Semaphore inFlight = new Semaphore(maxInFlight);

		// 4) "끝난 작업부터" 회수 가능한 CompletionService 준비
		final CompletionService<Boolean> cs = new ExecutorCompletionService<>(placeImportExecutor);

		int submitted = 0; // 제출된 작업 수(emdCd 누락 스킵 제외)
		int skipped = 0;   // emdCd 누락 등으로 제출하지 않은 행 수

		// 5) 작업 제출(Submit)
		for (var row : rows) {
			// emdCd 필수: 누락 시 로그만 남기고 스킵
			if (row.getEmdCd() == null || row.getEmdCd().isBlank()) {
				skipped++;
				log.warn("[PlaceImport] skip title='{}' reason=emdCd 누락", row.getTitle());
				continue;
			}

			// 제출량 제한: permit 없으면 잠시 대기 (큐 적체 방지)
			inFlight.acquireUninterruptibly();

			// 스레드풀에 비동기 제출. Boolean 반환: 성공 true / 실패 false
			cs.submit(() -> {
				try {
					// 실제 저장 로직: REQUIRES_NEW 트랜잭션으로 독립 실행
					rowImporter.importOne(row, admin);
					return true;
				} catch (Exception e) {
					// 특정 행의 오류는 전체 배치를 멈추지 않음
					log.warn("[PlaceImport] skip title='{}': {}", row.getTitle(), e.getMessage());
					return false;
				} finally {
					// 작업 종료 시 반드시 permit 반환
					inFlight.release();
				}
			});
			submitted++;
		}

		// 6) 작업 회수(Collect): 끝난 작업부터 꺼내 성공 건수 saved 집계
		int saved = 0;
		try {
			for (int i = 0; i < submitted; i++) {
				try {
					// 완료된 Future가 올 때까지 블로킹 대기
					if (Boolean.TRUE.equals(cs.take().get())) {
						saved++;
					}
				} catch (InterruptedException ie) {
					// 인터럽트 발생: 현재 스레드의 인터럽트 상태 복원 후 부분 결과만 반환
					Thread.currentThread().interrupt();
					log.error("[PlaceImport] 인터럽트 감지. 부분 결과만 반환합니다. collected={}/{}", i, submitted);
					break;
				} catch (ExecutionException ee) {
					// submit 내부에서 이미 warn을 남겼으므로 요약만 로깅
					log.warn("[PlaceImport] worker task failed: {}",
							 ee.getCause() == null ? ee.toString() : ee.getCause().toString());
				}

				// 1,000건마다 진행률 로그 출력(소요시간/처리량 포함)
				if ((i + 1) % 1_000 == 0 || (i + 1) == submitted) {
					double sec = (System.nanoTime() - startNs) / 1_000_000_000.0;
					double tps = sec > 0 ? saved / sec : 0.0;
					log.info("[PlaceImport] progress {}/{} (saved={}, skipped={}) {}s, {} rows/s",
							 i + 1, submitted, saved, skipped,
							 String.format("%.2f", sec), String.format("%.1f", tps));
				}
			}
		} finally {
			// 주의: placeImportExecutor는 Spring이 관리하므로 여기서 shutdown 하지 않음
			// (앱 전체에서 재사용되는 Bean일 가능성이 큼)
		}

		// 7) 최종 성능 요약 로그
		double totalSec = (System.nanoTime() - startNs) / 1_000_000_000.0;
		log.info("[PlaceImport] done: totalRows={}, submitted={}, saved={}, skipped={}, time={}s, throughput={} rows/s",
				 total, submitted, saved, skipped,
				 String.format("%.2f", totalSec), String.format("%.1f", saved / Math.max(0.001, totalSec)));

		return saved;
	}
}
