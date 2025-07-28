package com.explorer.gabom.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
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
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		Place savedPlace = placeRepository.save(place);

		return new PlaceCreateResponse(savedPlace.getId());
	}

	// 탐험 장소 리스트 조회(검색)

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
	@Transactional
	public void deletePlace(Long placeId, Long userId) {
		Place place = placeRepository.findByIdAndStatusInAndDeletedAtIsNull(placeId, List.of(PlaceStatus.PENDING, PlaceStatus.APPROVED))
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		if (!place.getUser().getId().equals(userId)) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}

		place.markAsDeleted(); // 실제 삭제하지 않고 status 변경
	}
}
