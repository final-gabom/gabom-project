package com.explorer.gabom.domain.place.repository;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final PlaceRepository placeRepository;

	@Override
	public Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		QPlace place = QPlace.place;

		long updated = queryFactory.update(place)
								   .set(place.title, request.getTitle())
								   .set(place.address, request.getAddress())
								   .set(place.lat, request.getLat())
								   .set(place.lng, request.getLng())
								   .set(place.proofMethod, request.getProofMethod())
								   .set(place.content, request.getContent())
								   .where(place.id.eq(placeId).and(place.user.id.eq(userId)))
								   .execute();

		if (updated == 0) return null;

		return placeRepository.findById(placeId)
							  .orElseThrow(() -> new IllegalArgumentException("업데이트 후 Place 조회를 실패했습니다."));
	}
}
