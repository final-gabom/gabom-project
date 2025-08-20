package com.explorer.gabom.domain.place.repository;

import static com.explorer.gabom.domain.address.entity.QAddress.*;
import static com.explorer.gabom.domain.place.entity.QPlace.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.QAddress;
import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.missionproof.entity.QMissionProof;
import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.dto.request.PlaceSearchCond;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.QPlaceFile;
import com.explorer.gabom.domain.place.mapper.PlaceSummaryMapper;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.entity.QUser;
import com.explorer.gabom.global.dto.PageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Transactional(readOnly = true)
	@Override
	public List<Long> findPlaceIdsForSummary(PlaceSearchCond cond) {
		QUser writer = QUser.user;
		QMissionProof mp = QMissionProof.missionProof;
		QAddress addr = address;

		BooleanBuilder where = new BooleanBuilder()
			.and(place.deletedAt.isNull())
			.and(getAddressFilter(cond))
			.and(getKeywordFilter(cond.getKeyword()));

		NumberExpression<Double> distanceExpr = getOptionalDistanceExpr(cond, addr);
		NumberExpression<Long> proofCountExpr = mp.id.countDistinct().coalesce(0L);

		JPAQuery<Long> query = queryFactory.select(place.id)
										   .from(place)
										   .join(place.user, writer)
										   .leftJoin(addr).on(place.addressId.eq(addr.id))
										   .leftJoin(mp).on(mp.place.eq(place), mp.deletedAt.isNull())
										   .where(where);

		applySortBase(query, cond.getPageable(), distanceExpr, proofCountExpr);

		if (distanceExpr != null || containsSortField(cond, "proofCount")) {
			query.groupBy(place.id);
		}

		return query.offset(cond.getPageable().getOffset())
					.limit(cond.getPageable().getPageSize())
					.fetch();
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<PlaceSummary> fetchPlaceSummariesByIds(List<Long> placeIds, PlaceSearchCond cond) {
		if (placeIds == null || placeIds.isEmpty()) {
			return PageResponse.toDto(Page.empty(cond.getPageable()));
		}

		QPlaceFile placeFile = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QAttachmentFile subFile = new QAttachmentFile("sf");
		QUser writer = QUser.user;
		QTitle title = QTitle.title;
		QMissionProof mp = QMissionProof.missionProof;
		QAddress addr = address;

		NumberExpression<Long> proofCountExpr = mp.id.countDistinct().coalesce(0L);
		NumberExpression<Double> distanceExpr = getOptionalDistanceExpr(cond, addr);

		JPAQuery<Tuple> query = queryFactory.select(
												place.id, place.title, address, address.lat, address.lng,
												place.viewCount, writer.id, writer.nickname, writer.level,
												writer.title.name, file.fileId, file.filePath,
												distanceExpr != null ? distanceExpr.as("distance") : Expressions.nullExpression(Double.class),
												proofCountExpr.as("proofCount")
											)
											.from(place)
											.join(place.user, writer)
											.leftJoin(writer.title, title)
											.leftJoin(address).on(place.addressId.eq(address.id))
											.leftJoin(placeFile).on(placeFile.place.eq(place))
											.leftJoin(mp).on(mp.place.eq(place), mp.deletedAt.isNull())
											.leftJoin(file).on(file.eq(placeFile.file),
															   file.fileId.eq(JPAExpressions.select(subFile.fileId)
																							.from(subFile)
																							.join(placeFile)
																							.on(placeFile.file.eq(
																								subFile))
																							.where(placeFile.place.eq(
																									   place),
																								   subFile.deleted.isFalse())
																							.orderBy(
																								subFile.orderIdx.asc())
																							.limit(1)))
											.where(place.deletedAt.isNull(), place.id.in(placeIds))
											.groupBy(Stream.of(
												place.id, place.title, address.id, address.lat, address.lng,
												place.viewCount, writer.id, writer.nickname, writer.level,
												writer.title.name, file.fileId, file.filePath
											).toArray(Expression[]::new));

		applySortBase(query, cond.getPageable(), distanceExpr, proofCountExpr);

		List<Tuple> tuples = query.offset(cond.getPageable().getOffset())
								  .limit(cond.getPageable().getPageSize()).fetch();

		Long total = Optional.ofNullable(queryFactory.select(place.count())
													 .from(place)
													 .where(place.deletedAt.isNull(), place.id.in(placeIds))
													 .fetchOne()).orElse(0L);

		List<PlaceSummary> content = tuples.stream()
										   .map(PlaceSummaryMapper::fromTuple).toList();

		return PageResponse.toDto(new PageImpl<>(content, cond.getPageable(), total));
	}

	@Override
	public List<Tuple> findWithinRadius(double lat, double lon, double minKm, double maxKm) {
		NumberExpression<Double> distMeter = getDistanceExpression(lat, lon, address.lat, address.lng);
		return queryFactory.select(place.id, distMeter.divide(1_000.0))
						   .from(place)
						   .leftJoin(address).on(place.addressId.eq(address.id))
						   .where(
							   place.deletedAt.isNull(),
							   address.lat.isNotNull(),
							   address.lng.isNotNull(),
							   distMeter.between(minKm * 1000, maxKm * 1000))
						   .fetch();
	}

	private <T> void applySortBase(JPAQuery<T> query, Pageable pageable,
								   NumberExpression<Double> distanceExpr,
								   NumberExpression<Long> proofCountExpr) {
		PathBuilder<Place> entityPath = new PathBuilder<>(Place.class, "place");
		Set<String> allowed = Set.of("viewCount", "createdAt", "updatedAt");

		for (Sort.Order order : pageable.getSort()) {
			String prop = order.getProperty();
			boolean asc = order.isAscending();
			OrderSpecifier<?> spec = switch (prop) {
				case "distance" -> distanceExpr != null ? new OrderSpecifier<>(asc ? Order.ASC : Order.DESC,
																			   distanceExpr) : null;
				case "proofCount" -> proofCountExpr != null ? new OrderSpecifier<>(asc ? Order.ASC : Order.DESC,
																				   proofCountExpr) : null;
				default -> allowed.contains(prop) ?
						   new OrderSpecifier<>(asc ? Order.ASC : Order.DESC,
												entityPath.getComparable(prop, Comparable.class)) : null;
			};
			if (spec != null)
				query.orderBy(spec);
		}

		query.orderBy(place.id.asc());
	}

	private BooleanExpression getAddressFilter(PlaceSearchCond cond) {
		return Optional.ofNullable(cond.getEmdCd()).filter(s -> !s.isBlank()).map(address.emdCd::eq)
					   .or(() -> Optional.ofNullable(cond.getSggCd()).filter(s -> !s.isBlank()).map(address.sggCd::eq))
					   .or(() -> Optional.ofNullable(cond.getSdCd()).filter(s -> !s.isBlank()).map(address.sdCd::eq))
					   .orElse(null);
	}

	private BooleanExpression getKeywordFilter(String keyword) {
		return (keyword == null || keyword.isBlank()) ? null :
			   place.title.containsIgnoreCase(keyword).or(address.detail.containsIgnoreCase(keyword));
	}

	private NumberExpression<Double> getDistanceExpression(double lat, double lng,
														   NumberPath<Double> targetLat, NumberPath<Double> targetLng) {
		return Expressions.numberTemplate(Double.class,
										  "ST_Distance_Sphere(point({1}, {0}), point({3}, {2}))",
										  lat, lng, targetLat, targetLng);
	}

	private NumberExpression<Double> getOptionalDistanceExpr(PlaceSearchCond cond, QAddress addr) {
		return (cond.getLat() != null && cond.getLng() != null)
			   ? getDistanceExpression(cond.getLat(), cond.getLng(), addr.lat, addr.lng).divide(1000.0)
			   : null;
	}

	private boolean containsSortField(PlaceSearchCond cond, String field) {
		return cond.getPageable().getSort().stream()
				   .anyMatch(order -> order.getProperty().equals(field));
	}
}
