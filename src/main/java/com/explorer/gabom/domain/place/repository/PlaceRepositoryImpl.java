package com.explorer.gabom.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceListResponse;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

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

	@Override
	public List<PlaceListResponse> findPlacesByDistanceAndQuery(Long userId, Sort sort, Double lat, Double lng, String  query, Long  lastId, Integer size) {
		QPlace place = QPlace.place;

		BooleanBuilder builder = new BooleanBuilder();

		// soft delete
		builder.and(place.deletedAt.isNull());

		if (query != null && !query.trim().isEmpty()) {
			builder.and(
				place.title.containsIgnoreCase(query)
						   .or(place.address.containsIgnoreCase(query))
			);
		}

		// 커서 페이징
		if (lastId != null) {
			builder.and(place.id.lt(lastId));
		}

		// Haversine 거리 계산
		NumberExpression<Double> distanceExpr = Expressions.numberTemplate(
			Double.class,
			"6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
			lat, place.lat, lng, place.lng
		);

		// 정렬 조건
		OrderSpecifier<?> orderSpecifier = place.id.desc(); // 기본값
		if (sort != null && sort.isSorted()) {
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			boolean isAsc = order.getDirection().isAscending();

			if ("createdAt".equals(property)) {
				orderSpecifier = isAsc ? place.createdAt.asc() : place.createdAt.desc();
			} else if ("title".equals(property)) {
				orderSpecifier = isAsc ? place.title.asc() : place.title.desc();
			}
		}

		// DTO 바로 생성해서 반환
		return queryFactory
			.select(Projections.constructor(PlaceListResponse.class,
											place.id,
											place.title,
											place.address,
											Expressions.constant(0),
											place.content,
											distanceExpr
			))
			.from(place)
			.where(builder)
			.orderBy(orderSpecifier)
			.limit(size != null ? size : 10)
			.fetch();
	}
}
