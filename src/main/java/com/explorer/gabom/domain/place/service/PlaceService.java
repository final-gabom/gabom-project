package com.explorer.gabom.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

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
	@Transactional
	public void updatePlace(Long placeId, PlaceUpdateRequest request, User user) {
		Place place = placeRepository.findById(placeId)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 작성자 확인
		if (!place.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}

		place.updatePlace(
			request.getTitle(),
			request.getAddress(),
			request.getLat(),
			request.getLng(),
			request.getContent(),
			request.getProofMethod()
		);
	}

	// 탐험 장소 삭제
}
