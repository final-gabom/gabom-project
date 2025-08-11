package com.explorer.gabom.domain.batch;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AddressNameParser {

	/**
	 * 예시 입력: "서울특별시 서초구 반포동 115-5"
	 *
	 * 정규식 설명
	 *  - (?<sd> ... ): 시/도 캡처
	 *      - 공백이 아닌 문자 1개 이상 + 아래 접미사 중 하나로 끝나는 토큰
	 *      - 접미사: 특별자치도 | 특별시 | 광역시 | 자치시 | 도
	 *      - 예: 서울특별시, 세종특별자치시, 강원특별자치도, 경기도
	 *
	 *  - (?<sgg> ... ): 시군구 캡처
	 *      - 공백이 아닌 문자 1개 이상 + (시|군|구)로 끝나는 토큰
	 *      - 예: 종로구, 서초구, 고양시, 양평군
	 *
	 *  - (?<emd> ... ): 읍면동 캡처
	 *      - 공백이 아닌 문자 1개 이상 + (동|읍|면)로 끝나는 토큰
	 *      - 예: 반포동, 공릉동, 다산동, 가평읍, 청기면
	 *
	 *  - (?<detail>.*): 나머지 상세주소 (있어도 되고 없어도 됨)
	 *
	 * 주의: 도로명주소(…로/…길/…대로)처럼 동/읍/면으로 끝나지 않는 경우는 여기서 매칭 실패 → 아래 폴백 로직으로 이동
	 */
	private static final Pattern P = Pattern.compile(
		"^(?<sd>[^\\s]+(?:특별자치도|특별시|광역시|자치시|도))\\s+" +   // 시/도
			"(?<sgg>[^\\s]+(?:시|군|구))\\s+" +                            // 시군구
			"(?<emd>[^\\s]+(?:동|읍|면))\\s*" +                            // 읍면동
			"(?<detail>.*)$"                                              // 상세
	);

	private AddressNameParser() {}

	/**
	 * 전체 주소 문자열을 시/도, 시군구, 읍면동, 상세로 분해한다.
	 * 1) 정규식으로 정확 매칭을 시도
	 * 2) 실패 시 폴백: 앞 3개 토큰을 단순히 시/군/동으로 해석
	 *    - 도로명주소처럼 “동/읍/면”이 아닌 토큰이 들어와도 3번째를 동으로 간주
	 *    - 이후 단계(Resolver)에서 DB 매칭 실패 시 placeholder 등을 활용
	 */
	public static Parsed parse(String full) {
		if (full == null || full.isBlank()) {
			throw new IllegalArgumentException("주소 없음");
		}
		String trimmed = full.trim();

		// 1) 정규식 매칭
		Matcher m = P.matcher(trimmed);
		if (m.find()) {
			return new Parsed(
				m.group("sd"),
				m.group("sgg"),
				m.group("emd"),
				opt(m.group("detail"))
			);
		}

		// 2) 폴백: 앞 3개 토큰을 시/군/동으로 간주
		//    - "서울특별시 종로구 세종로 1-68" 같은 도로명주소도 여기로 옴
		//    - 이후 Resolver에서 endsWith/contains 검색 + placeholder로 보완
		String[] tok = trimmed.split("\\s+");
		if (tok.length < 3) {
			throw new IllegalArgumentException("주소 파싱 실패: " + full);
		}
		String detail = tok.length > 3 ? String.join(" ", Arrays.copyOfRange(tok, 3, tok.length)) : "";
		return new Parsed(tok[0], tok[1], tok[2], detail);
	}

	private static String opt(String s) { return s == null ? "" : s.trim(); }

	/** 파싱 결과 레코드: 시/도명, 시군구명, 읍면동명(또는 3번째 토큰), 상세 */
	public record Parsed(String sdNm, String sggNm, String emdNm, String detail) {}
}


