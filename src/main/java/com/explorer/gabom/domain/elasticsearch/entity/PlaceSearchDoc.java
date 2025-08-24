package com.explorer.gabom.domain.elasticsearch.entity;

import lombok.*;
import java.time.Instant;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceSearchDoc {

	private String id;          // ES 문서 ID = Place.id
	private String title;       // 검색 가산치용
	private String addressFull; // 자유 검색(주소)
	private String emdCd;       // 필터(정확 term)
	private Long popularity;    // 정렬(조회수 등)
	private String status;      // 필터(승인 등)
	private Instant createdAt;  // 정렬(최신)
	private Double lat;         // 반경 검색 옵션
	private Double lon;

	public Map<String, Object> toMap() {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		m.put("title", title);
		m.put("address_full", addressFull);
		m.put("emdCd", emdCd);
		m.put("popularity", popularity);
		m.put("status", status);

		// createdAt 직렬화: ISO 문자열(권장) 또는 epochMillis 중 택1
		if (createdAt != null) {
			m.put("createdAt", createdAt.toString());          // ISO-8601 문자열
			// m.put("createdAt", createdAt.toEpochMilli());   // epoch millis로 쓰고 싶을 때
		}

		if (lat != null && lon != null) {
			m.put("location", Map.of("lat", lat, "lon", lon));
		}

		// null 제거 (ES에 null 필드 굳이 안 보냄)
		m.values().removeIf(Objects::isNull);
		return m;
	}
}
