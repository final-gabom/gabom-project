package com.explorer.gabom.domain.place.repository;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final PlaceRepository placeRepository;

	@Override
	public Place updatePlace(Long placeId, Long userId, PlaceUpdateRequest request) {
		QPlace place = QPlace.place;

		// 동적 업데이트
		JPAUpdateClause update = queryFactory.update(place);
		int setCount = 0;

		if (request.getTitle() != null) {
			update.set(place.address, request.getAddress());
			setCount++;
		}
		if (request.getAddress() != null) {
			update.set(place.address, request.getAddress());
			setCount++;
		}
		if (request.getLat() != null) {
			update.set(place.lat, request.getLat());
			setCount++;
		}
		if (request.getLng() != null) {
			update.set(place.lng, request.getLng());
			setCount++;
		}
		if (request.getProofMethod() != null) {
			update.set(place.proofMethod, request.getProofMethod());
			setCount++;
		}
		if (request.getContent() != null) {
			update.set(place.content, request.getContent());
			setCount++;
		}

		// 수정할 값이 하나도 없으면 예외
		if (setCount == 0) {
			throw new CustomException(ErrorCode.NO_FIELDS_TO_UPDATE);
		}

		long updated = update
			.where(
				place.id.eq(placeId),
				place.user.id.eq(userId)
			)
			.execute();

		// 서비스에서 PLACE_NO_PERMISSION 처리
		if (updated == 0) {
			return null;
		}

		// 최종 상태 재조회 후 반환
		return queryFactory
			.selectFrom(place)
			.where(place.id.eq(placeId))
			.fetchOne();
		}
}
