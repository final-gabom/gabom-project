package com.explorer.gabom.domain.place.repository;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.entity.Place;

public interface PlaceRepositoryCustom {

	Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request);
}