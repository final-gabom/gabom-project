package com.explorer.gabom.domain.place.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	// 탐험 장소 생성
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, User user) {

		Place place = new Place(request, user);

		Place savedPlace = placeRepository.save(place);

		return new PlaceCreateResponse(savedPlace.getId());
	}

	// 탐험 장소 리스트 조회(검색)

	// 탐험 장소 상세 조회

	// 탐험 장소 수정


	// 탐험 장소 삭제
}
