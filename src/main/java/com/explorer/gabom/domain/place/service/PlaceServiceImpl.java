package com.explorer.gabom.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.OffsetDto;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;


	@Override
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId) {
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		place.approve(); // 기본 승인 처리

		Place savedPlace = placeRepository.save(place);
		return new PlaceCreateResponse(savedPlace.getId());
	}

	@Override
	@Transactional
	public OffsetDto<PlaceSummary> getPlaceList(Sort sort, String query, Double lat, Double lng, Long lastId,
												Integer size) {
		return placeRepository.findPlaceSummaries(sort, query, lat, lng, lastId, size);
	}

	@Override
	@Transactional
	public void updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		Place updatedPlace = placeRepository.updatePlace(placeId, userId, request);
		if (updatedPlace == null) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}
	}

	@Override
	@Transactional
	public void deletePlace(Long placeId, Long userId) {
		Place place = placeRepository.findByIdAndStatusInAndDeletedAtIsNull(
										 placeId, List.of(PlaceStatus.PENDING, PlaceStatus.APPROVED))
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		if (!place.getUser().getId().equals(userId)) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}

		place.markAsDeleted(); // Soft Delete 처리

	}
}
