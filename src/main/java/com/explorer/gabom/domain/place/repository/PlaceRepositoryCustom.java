package com.explorer.gabom.domain.place.repository;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.global.dto.PageResponse;

public interface PlaceRepositoryCustom {

	Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	PageResponse<PlaceSummary> findPlaceSummaries(String keyword, Double lat, Double lng, Pageable pageable);
}
