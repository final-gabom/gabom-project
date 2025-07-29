package com.explorer.gabom.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.OffsetDto;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.title.entity.QUserTitle;
import com.explorer.gabom.domain.user.entity.QUser;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	protected final JPAQueryFactory queryFactory;

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
	public OffsetDto<PlaceSummary> findPlaceSummaries(Sort sort, String query, Double lat, Double lng, Long lastId,
													  Integer size) {
		QPlace place = QPlace.place;
		QUser user = QUser.user;
		QUserTitle userTitle = QUserTitle.userTitle;
		QTitle title = QTitle.title;

		// 삭제되지 않은 장소만 조회
		BooleanBuilder builder = new BooleanBuilder().and(place.deletedAt.isNull());

		// 장소 이름 검색 & 주소 검색
		if (query != null && !query.isBlank()) {
			builder.and(place.title.containsIgnoreCase(query)
				   .or(place.address.containsIgnoreCase(query)));
		}

		// 커서 조건 동적 구성
		if (lastId != null) {
			BooleanExpression cursorCondition = buildCursorCondition(orderList, place, lastId, lat, lng);
			if (cursorCondition != null) {
				builder.and(cursorCondition);
			}
		}

		JPAQuery<PlaceSummary> baseQuery = queryFactory
			.select(Projections.constructor(PlaceSummary.class,
											place.id,
											place.title,
											place.address,
											place.lat,
											place.lng,
											Expressions.constant("https://dummy.image.url"), // imageUrl
											Expressions.constant(0), // proofCount
											Expressions.constant(0.0), // avgRating
											place.viewCount,
											user.id,
											user.nickname,
											user.level,
											title.name
			))
			.from(place)
			.join(place.user, user)
			.leftJoin(userTitle).on(userTitle.user.eq(user))
			.leftJoin(userTitle.title, title)
			.where(builder);

		// ✅ 거리 기반 정렬 여부 확인
		Sort.Order distanceOrder = sort.getOrderFor("distance");

		List<PlaceSummary> content;


		NumberExpression<Double> distanceExpr = getDistanceExpression(lat, lng, place.lat, place.lng);



		return new OffsetDto<>(
			content,
			null, size, null, null, hasNext
		);
	}

	private BooleanExpression buildCursorCondition(Sort sort, QPlace place, Long lastId, Double lat, Double lng) {
		// 정렬 필드 목록 구성 (id 보조 정렬 필드 항상 추가)
		if (sort.stream().noneMatch(order -> order.getProperty().equals("id"))) {
			sort = sort.and(Sort.by("id").descending());
		}

		BooleanExpression condition = null;


	}

	private NumberExpression<Double> getDistanceExpression(double lat, double lng, NumberPath<Double> targetLat,
														   NumberPath<Double> targetLng) {
		return Expressions.numberTemplate(
			Double.class,
			"6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
			lat, targetLat, lng, targetLng
		).as("distance");
	}
}
