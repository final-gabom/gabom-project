package com.explorer.gabom.domain.place.repository;

import org.springframework.data.domain.Sort;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.OffsetDto;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepositoryCustom {

	Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	OffsetDto<PlaceSummary> findPlaceSummaries(Sort sort, String query, Double lat, Double lng, Long lastId, Integer size);
}
