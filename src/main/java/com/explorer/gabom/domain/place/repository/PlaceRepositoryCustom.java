package com.explorer.gabom.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.dto.request.PlaceSearchCond;
import com.explorer.gabom.global.dto.PageResponse;
import com.querydsl.core.Tuple;

public interface PlaceRepositoryCustom {

	PageResponse<PlaceSummary> findPlaceSummaries(PlaceSearchCond cond);

	/** ES에서 받은 ids로 얇은 요약 상세 조회 */
	List<PlaceSummary> findSummariesByIds(List<Long> ids);

	/** 키워드 없을 때: DB 경로에서 ID만 페이지로 */
	List<Long> findPlaceIdsForSummaryWithoutKeyword(String emdCd, Pageable pageable);

	/** 키워드 없을 때: 전체 개수 */
	long countForSummaryWithoutKeyword(String emdCd);

	/**
	 * 주어진 위도(lat), 경도(lon) 기준으로
	 * minKm ≤ 거리(km) < maxKm 인 모든 장소의 (id, distanceKm) 튜플을 반환한다.
	 */
	List<Tuple> findWithinRadius(double lat, double lon, double minKm, double maxKm);
}
