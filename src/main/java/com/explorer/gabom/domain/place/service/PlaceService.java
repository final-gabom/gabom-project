package com.explorer.gabom.domain.place.service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.PlaceDetail;
import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.dto.response.PlaceUpdateResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.PageResponse;

public interface PlaceService {

	@Transactional
	PlaceCreateResponse createPlace(PlaceCreateRequest request, User user);

	@Transactional
	PlaceDetail getPlaceDetail(Long placeId);

	@Transactional
	PageResponse<PlaceSummary> getPlaceList(String query, Double lat, Double lng, Pageable pageable);

	@Transactional
	PlaceUpdateResponse updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	@Transactional
	Long deletePlace(Long placeId, Long userId);
}