package com.explorer.gabom.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceDetailResponse;
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
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		Place savedPlace = placeRepository.save(place);

		return new PlaceCreateResponse(savedPlace.getId());
	}

	// 탐험 장소 리스트 조회(검색)
	public Page<PlaceListResponse> getPlaceList(String query, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
		return placeRepository.findByDeletedAtIsNullAndTitleContainingOrDeletedAtIsNullAndAddressContaining(query, query, pageable).map(PlaceListResponse::from);
	}

	// 탐험 장소 상세 조회
	public PlaceDetailResponse getPlaceDetail(Long placeId) {
		Place place = placeRepository.findByIdAndDeletedAtIsNull(placeId)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		return PlaceDetailResponse.from(place);
	}

	// 탐험 장소 수정
	@Transactional
	public void updatePlace(Long placeId, PlaceUpdateRequest request, Long userId) {
		Place place = placeRepository.findById(placeId).orElseThrow(
			() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 작성자 확인
		if (!place.getUser().getId().equals(userId)) {
			throw new CustomException(ErrorCode.PLACE_NO_PERMISSION);
		}

		place.updatePlace(request.getTitle(), request.getAddress(), request.getLat(), request.getLng(),
						  request.getContent(), request.getProofMethod());
	}

	// 탐험 장소 삭제
}
