package com.explorer.gabom.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceDetail;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.validator.AuthorValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;
	private final AuthorValidator authorValidator;

	@Override
	@Transactional
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId) {
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		place.approve(); // 기본 승인 처리, 추후 로직에 따라 변경 필요

		Place savedPlace = placeRepository.save(place);
		return PlaceCreateResponse.toDto(savedPlace);
	}

	// 탐험 장소 상세 조회
	@Transactional
	@Override
	public PlaceDetail getPlaceDetail(Long placeId) {
		// 1) 엔티티 조회 & 예외 처리
		Place place = placeRepository.findByIdAndStatus(placeId, PlaceStatus.APPROVED)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 2) 조회수 증가
		place.increaseViewCount();

		// 3) DTO 변환 (toDto 내부에서 UserSummaryDto, FileResponseDto, missionProofCount, avgScore 등 모두 처리)
		return PlaceDetail.toDto(place);
	}

	@Transactional
	@Override
	public PageResponse<PlaceSummary> getPlaceList(String query, Double lat, Double lng, Pageable pageable) {
		return placeRepository.findPlaceSummaries(query, lat, lng, pageable);
	}

	@Transactional
	@Override
	public PlaceDetail updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		Place place = placeRepository.findById(placeId)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
		authorValidator.validateOwner(
			place.getUser().getId(),
			userId
		);

		Place updatedPlace = place.update(request);
		Place savedPlace = placeRepository.save(updatedPlace);

		return PlaceDetail.toDto(savedPlace);
	}

	@Transactional
	@Override
	public Long deletePlace(Long placeId, Long userId) {
		Place place = placeRepository.findByIdAndStatusInAndDeletedAtIsNull(
										 placeId, List.of(PlaceStatus.PENDING, PlaceStatus.APPROVED))
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		authorValidator.validateOwner(
			place.getUser().getId(),
			userId
		);

		place.markAsDeleted(); // Soft Delete 처리

		return place.getId();
	}
}
