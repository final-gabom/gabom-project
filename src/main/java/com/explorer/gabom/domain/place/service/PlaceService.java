package com.explorer.gabom.domain.place.service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceDetailResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.global.dto.PageResponse;

public interface PlaceService {

	@Transactional
	PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId);

	@Transactional
	PlaceDetailResponse getPlaceDetail(Long placeId);

	@Transactional
	PageResponse<PlaceSummary> getPlaceList(String query, Double lat, Double lng, Pageable pageable);

	@Transactional
	void updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	@Transactional
	Long deletePlace(Long placeId, Long userId);
}