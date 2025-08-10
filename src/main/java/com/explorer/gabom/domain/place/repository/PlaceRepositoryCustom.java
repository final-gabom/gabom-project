package com.explorer.gabom.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.global.dto.PageResponse;
import com.querydsl.core.Tuple;

public interface PlaceRepositoryCustom {

	PageResponse<PlaceSummary> findPlaceSummaries(String keyword, Double lat, Double lng, Pageable pageable);

	/**
	 * 주어진 위도(lat), 경도(lon) 기준으로
	 * minKm ≤ 거리(km) < maxKm 인 모든 장소의 (id, distanceKm) 튜플을 반환한다.
	 */
	List<Tuple> findWithinRadius(double lat, double lon, double minKm, double maxKm);
}
