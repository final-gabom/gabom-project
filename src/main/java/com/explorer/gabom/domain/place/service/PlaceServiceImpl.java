package com.explorer.gabom.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.service.AddressService;
import com.explorer.gabom.domain.address.type.AddressType;
import com.explorer.gabom.domain.place.dto.PlaceDetail;
import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceSearchCond;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceUpdateResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.validator.AuthorValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final AuthorValidator authorValidator;
	private final AddressService addressService;

	@Override
	@Transactional
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, User user) {
		// 1. Place 먼저 저장 → placeId 확보
		Place place = new Place(request, user);
		place.approve();    // 초기 설정 무조건 등록
		placeRepository.save(place); // 이 시점에서 place.getId() 사용 가능

		// 2. Address 추가 요청 Dto 생성
		AddressRequest addressRequest = AddressRequest.builder()
													  .addressTypeCd(AddressType.PLACE)
													  .targetId(place.getId())
													  .emdCd(request.getEmdCd())
													  .addressDetail(request.getAddressDetail())
													  .lat(request.getLat())
													  .lng(request.getLng())
													  .build();

		AddressDto savedAddr = addressService.createOrReplace(addressRequest);

		// 3. addressId 세팅하고 Place 다시 저장
		place.setAddressId(savedAddr.getId());
		Place saved = placeRepository.save(place);

		return PlaceCreateResponse.toDto(saved, savedAddr);
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
	public PageResponse<PlaceSummary> getPlaceList(PlaceSearchCond cond) {
		List<Long> placeIdsForSummary = placeRepository.findPlaceIdsForSummary(cond);
		return placeRepository.fetchPlaceSummariesByIds(placeIdsForSummary, cond);
	}

	@Transactional
	@Override
	public PlaceUpdateResponse updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		Place place = placeRepository.findById(placeId)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 작성자 검증
		authorValidator.validateOwner(place.getUser().getId(), userId);

		place.update(request);

		// 주소 처리
		AddressDto addressDto;
		if (request.getAddress() != null) {
			AddressRequest address = request.getAddress();
			address.setAddressTypeCd(AddressType.PLACE);
			address.setTargetId(place.getId());

			AddressDto savedAddr = addressService.createOrReplace(address);
			place.setAddressId(savedAddr.getId());

			addressDto = savedAddr;
		} else {
			// 기존 addressId 기반으로 Address 조회
			addressDto = addressService.getByTypeAndTargetId(AddressType.PLACE, place.getId());
		}

		Place savedPlace = placeRepository.save(place);
		return PlaceUpdateResponse.toDto(savedPlace, addressDto);
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
