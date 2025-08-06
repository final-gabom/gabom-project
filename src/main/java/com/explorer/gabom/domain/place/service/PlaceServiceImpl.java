package com.explorer.gabom.domain.place.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceCreateResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceDetailResponse;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
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
	private final AttachmentFileRepository attachmentFileRepository;
	private final AuthorValidator authorValidator;

	@Override
	@Transactional
	public PlaceCreateResponse createPlace(PlaceCreateRequest request, Long userId) {
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Place place = new Place(request, user);
		place.approve(); // 기본 승인 처리

		Place savedPlace = placeRepository.save(place);
		return new PlaceCreateResponse(savedPlace.getId());
	}

	// 탐험 장소 상세 조회
	@Transactional
	@Override
	public PlaceDetailResponse getPlaceDetail(Long placeId) {
		Place place = placeRepository.findByIdAndStatus(placeId, PlaceStatus.APPROVED)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		place.increaseViewCount();

		User user = place.getUser();
		String userTitle = user.getTitle() != null ? user.getTitle().getName() : null;

		UserSummaryDto writer = UserSummaryDto.builder()
											  .id(user.getId())
											  .nickname(user.getNickname())
											  .level(user.getLevel())
											  .title(userTitle)
											  .build();

		// 🔥 파일 조회
		List<AttachmentFile> files = attachmentFileRepository.findByFileTypeAndRefIdAndDeletedFalse(FileType.PLACE,
																									place.getId());
		List<FileResponseDto> fileDtos = files.stream()
											  .map(FileResponseDto::toDto)
											  .collect(Collectors.toList());

		// TODO: toDto메소드 사용하여 mapping해주기
		return PlaceDetailResponse.builder()
								  .id(place.getId())
								  .title(place.getTitle())
								  .address(place.getAddress())
								  .lat(place.getLat())
								  .lng(place.getLng())
								  .missionProofCount(0) // 추후 구현
								  .avgScore(null)       // 추후 구현
								  .content(place.getContent()) // 🔥 본문 내용 추가
								  .viewCount(place.getViewCount())
								  .createdAt(place.getCreatedAt())
								  .updatedAt(place.getUpdatedAt())
								  .writer(writer)
								  .files(fileDtos) // 🔥 파일 DTO 포함
								  .build();
	}

	@Transactional
	@Override
	public PageResponse<PlaceSummary> getPlaceList(String query, Double lat, Double lng, Pageable pageable) {
		return placeRepository.findPlaceSummaries(query, lat, lng, pageable);
	}

	@Transactional
	@Override
	public void updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		Place place = placeRepository.findById(placeId)
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		authorValidator.validateOwner(
			place.getUser().getId(),
			userId
		);

		place.update(request);
	}

	@Transactional
	@Override
	public void deletePlace(Long placeId, Long userId) {
		Place place = placeRepository.findByIdAndStatusInAndDeletedAtIsNull(
										 placeId, List.of(PlaceStatus.PENDING, PlaceStatus.APPROVED))
									 .orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		authorValidator.validateOwner(
			place.getUser().getId(),
			userId
		);

		place.markAsDeleted(); // Soft Delete 처리

	}
}
