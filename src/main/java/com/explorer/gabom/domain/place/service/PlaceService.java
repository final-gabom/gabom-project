package com.explorer.gabom.domain.place.service;

import org.springframework.data.domain.Sort;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.OffsetDto;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;


public interface PlaceService {

	PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId);

	OffsetDto<PlaceSummary> getPlaceList(Sort sort, String query, Double lat, Double lng, Long lastId, Integer size);

	void updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);

	void deletePlace(Long placeId, Long userId);
}
