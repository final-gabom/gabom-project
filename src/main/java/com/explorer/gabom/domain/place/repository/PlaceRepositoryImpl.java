package com.explorer.gabom.domain.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.place.dto.request.PlaceUpdateRequest;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.domain.place.entity.QPlaceFile;
import com.explorer.gabom.domain.place.mapper.PlaceSummaryMapper;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.entity.QUser;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
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

		long updated = update.where(place.id.eq(placeId), place.user.id.eq(userId)).execute();

		// 서비스에서 PLACE_NO_PERMISSION 처리
		if (updated == 0) {
			return null;
		}

		// 최종 상태 재조회 후 반환
		return queryFactory.selectFrom(place).where(place.id.eq(placeId)).fetchOne();
	}

	@Override
	public PageResponse<PlaceSummary> findPlaceSummaries(String keyword, Double lat, Double lng, Pageable pageable) {
		QPlace place = QPlace.place;
		QPlaceFile placeFile = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QUser writer = QUser.user;
		QAttachmentFile subFile = new QAttachmentFile("sf");
		QTitle title = new QTitle("title");

		BooleanBuilder builder = getKeywordFilter(keyword).and(place.deletedAt.isNull());

		NumberExpression<Double> distanceExpr = (lat != null && lng != null)
												? getDistanceExpression(lat, lng, place.lat, place.lng)
													.divide(1000.0)        // ➜ km 단위
												: null;

		JPAQuery<Tuple> query = queryFactory
			.select(
				place.id,
				place.title,
				place.address,
				place.lat,
				place.lng,
				place.viewCount,
				writer.id,
				writer.nickname,
				writer.level,
				writer.title.name,
				file.fileId,
				file.filePath,
				distanceExpr.as("distance")
			)
			.from(place)
			.join(place.user, writer)
			.leftJoin(writer.title, title)
			.leftJoin(placeFile).on(placeFile.place.eq(place))
			.leftJoin(file).on(file.eq(placeFile.file),
							   file.fileId.eq(JPAExpressions.select(subFile.fileId)
															.from(subFile)
															.join(placeFile).on(placeFile.file.eq(subFile))
															.where(placeFile.place.eq(place), subFile.deleted.isFalse())
															.orderBy(subFile.orderIdx.asc())
															.limit(1)))
			.where(builder);

		applySort(query, pageable, distanceExpr);
		Long total = Optional.ofNullable(queryFactory
											 .select(place.count())
											 .from(place)
											 .join(place.user, writer)
											 .where(builder)
											 .fetchOne()).orElse(0L);

		List<Tuple> tuples = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

		List<PlaceSummary> content = tuples.stream()
										   .map(PlaceSummaryMapper::fromTuple)
										   .toList();

		return PageResponse.toDto(new PageImpl<>(content, pageable, total));
	}

	private void applySort(JPAQuery<Tuple> query, Pageable pageable, NumberExpression<Double> distanceExpr) {
		PathBuilder<Place> entityPath = new PathBuilder<>(Place.class, "place");

		for (Sort.Order order : pageable.getSort()) {
			if (order.getProperty().equals("distance") && distanceExpr != null) {
				query.orderBy(order.isAscending() ? distanceExpr.asc() : distanceExpr.desc());
			} else {
				query.orderBy(new OrderSpecifier<>(
					order.isAscending() ? Order.ASC : Order.DESC,
					entityPath.getComparable(order.getProperty(), Comparable.class)
				));
			}
		}
	}

	private BooleanBuilder getKeywordFilter(String keyword) {
		QPlace place = QPlace.place;
		if (keyword == null || keyword.isBlank())
			return new BooleanBuilder();
		return new BooleanBuilder().and(
			place.title.containsIgnoreCase(keyword)
					   .or(place.address.containsIgnoreCase(keyword))
		);
	}

	private NumberExpression<Double> getDistanceExpression(double lat, double lng, NumberPath<Double> targetLat,
														   NumberPath<Double> targetLng) {

		return Expressions.numberTemplate(
			Double.class,
			//   ST_Distance_Sphere(POINT(lon1, lat1), POINT(lon2, lat2))
			"ST_Distance_Sphere(point({1}, {0}), point({3}, {2}))",
			lat, lng,        // {0}: refLat  {1}: refLng
			targetLat, targetLng   // {2}: place.lat {3}: place.lng
		);
	}
}
