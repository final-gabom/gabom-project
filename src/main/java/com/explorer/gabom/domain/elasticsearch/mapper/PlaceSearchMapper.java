package com.explorer.gabom.domain.elasticsearch.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.elasticsearch.entity.PlaceSearchDoc;
import com.explorer.gabom.domain.place.entity.Place;

import io.micrometer.common.lang.Nullable;

/**
 * Place + Address 엔티티를 Elasticsearch 색인용 DTO(PlaceSearchDoc)로 변환하는 매퍼.
 * - 검색 품질을 위해 필요한 필드만 선별/가공한다.
 * - null 안전 처리 및 시간대(UTC) 통일을 여기서 한다.
 */
@Component
public class PlaceSearchMapper {

	/**
	 * JPA 엔티티를 ES 문서로 변환.
	 *
	 * @param p 장소 엔티티(필수)
	 * @param a 주소 엔티티(선택: null일 수 있음 — 과거 데이터/테스트 데이터 보호)
	 * @return PlaceSearchDoc (ES에 저장될 경량 문서)
	 */
	public PlaceSearchDoc toDoc(Place p, @Nullable Address a) {
		String full = null;      // ES에서 "주소 전체 문자열" 검색용 결합 필드
		Double lat = null, lon = null;
		String emd = null;

		// Address가 있을 때만 좌표/행정코드/주소문자열 세팅
		if (a != null) {
			emd = a.getEmdCd();
			lat = a.getLat();
			lon = a.getLng();

			// 주소 검색 품질 향상: 시도/시군구/읍면동/상세를 공백으로 연결
			// (null 제거 후 join)
			full = Stream.of(a.getSdCd(), a.getSggCd(), a.getEmdCd(), a.getDetail())
						 .filter(Objects::nonNull)
						 .collect(Collectors.joining(" "));
		}

		// createdAt은 UTC 기준 Instant로 고정
		//  - ES 정렬/필터에서 시간대 혼선을 방지
		Instant created = null;
		if (p.getCreatedAt() != null) {
			created = p.getCreatedAt().atZone(ZoneId.of("UTC")).toInstant();
		}

		// ES 문서 생성:
		//  - id         : Place PK를 문자열로 (ES 문서 ID로 사용)
		//  - title      : 검색 가중치용 필드
		//  - addressFull: 자유 텍스트 검색
		//  - emdCd      : 정확 일치(term) 필터용
		//  - popularity : 정렬(인기도) — 여기선 viewCount를 기반으로 Long 변환
		//  - status     : 상태(승인 등) 필터
		//  - createdAt  : 최신 정렬 등
		//  - lat/lon    : 위치 검색(반경/거리 정렬)을 위한 좌표
		return PlaceSearchDoc.builder()
							 .id(String.valueOf(p.getId()))
							 .title(p.getTitle())
							 .addressFull(full)
							 .emdCd(emd)
							 .popularity(p.getViewCount() != null ? p.getViewCount().longValue() : 0L)
							 .status(p.getStatus() != null ? p.getStatus().name() : null)
							 .createdAt(created)
							 .lat(lat)
							 .lon(lon)
							 .build();
	}
}
