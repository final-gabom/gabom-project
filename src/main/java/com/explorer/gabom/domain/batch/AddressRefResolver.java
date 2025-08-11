package com.explorer.gabom.domain.batch;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Eupmyeondong;
import com.explorer.gabom.domain.address.entity.Sido;
import com.explorer.gabom.domain.address.entity.Sigungu;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;
import com.explorer.gabom.domain.address.repository.SidoRepository;
import com.explorer.gabom.domain.address.repository.SigunguRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressRefResolver {

	// 행정구역 코드 테이블 조회용 리포지토리
	private final SidoRepository sidoRepo;
	private final SigunguRepository sggRepo;
	private final EupmyeondongRepository eupRepo;

	/**
	 * 주소 문자열 → 읍면동(Eupmyeondong) 엔티티 해석기.
	 *
	 * 동작 규칙
	 *  1) CSV가 emdCd(법정동 10자리)를 주면 그대로 사용(가장 확실).
	 *  2) 없으면 addressText를 파싱(시/도, 시군구, 읍면동 후보) → DB에서 단계적으로 조회.
	 *     - 시/도: 정확 일치(이름)로 조회
	 *     - 시군구: 정확 일치 → endsWith → contains 순으로 느슨 매칭
	 *     - 읍면동: 정확 일치 → startsWith → (마지막 폴백) “sggCd + 00000” 더미 코드(구 단위 placeholder)
	 *
	 * 예외 처리
	 *  - 찾지 못하면 IllegalArgumentException 발생 → 상위(배치)에서 catch 후 해당 행만 스킵.
	 */
	@Transactional(readOnly = true)
	public Eupmyeondong resolveEmd(String addressText, String optionalEmdCd) {
		// 1) emdCd가 명시되면 최우선 사용 (가장 신뢰도 높음)
		if (optionalEmdCd != null && !optionalEmdCd.isBlank()) {
			return eupRepo.findById(optionalEmdCd.trim())
						  .orElseThrow(() -> new IllegalArgumentException("읍면동 코드 없음: " + optionalEmdCd));
		}

		// 2) emdCd가 없으면 주소 문자열을 정규화 후 파싱
		if (addressText == null || addressText.isBlank()) {
			throw new IllegalArgumentException("주소 문자열 없음");
		}
		// 공백 정리(여러 공백 → 하나)
		String normalized = addressText.trim().replaceAll("\\s+", " ");

		// "시/도, 시군구, 읍면동(또는 3번째 토큰)" 분리
		var p = AddressNameParser.parse(normalized); // sdNm(), sggNm(), emdNm()

		// (1) 시/도: 정확 일치(이름 → 코드)
		Sido sd = sidoRepo.findBySdNm(p.sdNm().trim())
						  .orElseThrow(() -> new IllegalArgumentException("시도 없음: " + p.sdNm()));

		// (2) 시군구: 정확 일치 → endsWith → contains
		Sigungu sgg = sggRepo.findFirstBySggNmAndSdCd(p.sggNm().trim(), sd.getSdCd())
							 .orElseGet(() ->
											sggRepo.findFirstBySggNmEndsWithAndSdCd(p.sggNm().trim(), sd.getSdCd())
												   .orElseGet(() ->
																  sggRepo.findFirstBySggNmContainingAndSdCd(p.sggNm().trim(), sd.getSdCd())
																		 .orElseThrow(() ->
																						  new IllegalArgumentException("시군구 없음: " + p.sggNm() + " / addr=" + addressText)
																		 )
												   )
							 );

		// (3) 읍면동: 정확 일치 → startsWith → (폴백) sggCd + "00000" (구 단위 placeholder)
		Eupmyeondong emd = eupRepo.findByEmdNmAndSggCd(p.emdNm().trim(), sgg.getSggCd())
								  .orElseGet(() ->
												 eupRepo.findFirstByEmdNmStartsWithAndSggCd(p.emdNm().trim(), sgg.getSggCd())
														.orElseGet(() ->
																	   // 도로명주소 등으로 '동' 단위가 식별 안 되면
																	   // 구 단위 더미(예: 11110 + 00000 = 1111000000)로 폴백
																	   eupRepo.findById(sgg.getSggCd() + "00000")
																			  .orElseThrow(() ->
																							   new IllegalArgumentException("읍면동 없음: " + p.emdNm() + " / sgg=" + sgg.getSggNm())
																			  )
														)
								  );

		return emd;
	}
}

