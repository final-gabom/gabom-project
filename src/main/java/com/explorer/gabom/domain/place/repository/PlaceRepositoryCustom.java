package com.explorer.gabom.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceListResponse;
import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepositoryCustom {

	Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	// 거리 계산
	List<PlaceListResponse> findPlacesByDistanceAndQuery(Long userId, Sort sort, Double lat, Double lng, String query, Long lastId, Integer size);
}