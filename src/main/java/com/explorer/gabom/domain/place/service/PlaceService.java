package com.explorer.gabom.domain.place.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceListResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	// 탐험 장소 생성
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId) {
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		Place savedPlace = placeRepository.save(place);

		return new PlaceCreateResponse(savedPlace.getId());
	}

	// 탐험 장소 리스트 조회(검색)
	@Transactional(readOnly = true)
	public List<PlaceListResponse> getPlaceListByDistance(

		Long userId, String query, Sort sort, Double lat, Double lng, Long lastId, Integer size) {

		return placeRepository.findPlacesByDistanceAndQuery(userId, sort, lat, lng, query, lastId, size);
	}


	// 탐험 장소 상세 조회

	// 탐험 장소 수정
	@Transactional
	public void updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		Place updatedPlace = placeRepository.updatePlace(placeId, userId, request);
		if (updatedPlace == null) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}
	}

	// 탐험 장소 삭제
}
