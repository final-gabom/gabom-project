package com.explorer.gabom.domain.place.repository;

import static com.explorer.gabom.domain.address.entity.QAddress.*;
import static com.explorer.gabom.domain.place.entity.QPlace.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

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
import com.querydsl.core.types.ExpressionUtils;
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

	protected final JPAQueryFactory queryFactory;

	@Transactional(readOnly = true)
	@Override
	public PageResponse<PlaceSummary> findPlaceSummaries(PlaceSearchCond cond) {
		QPlaceFile placeFile = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QUser writer = QUser.user;
		QAttachmentFile subFile = new QAttachmentFile("sf");
		QTitle title = new QTitle("title");

		// soft delete & 주소 필터 & 키워드 검색 필터
		BooleanBuilder where = new BooleanBuilder()
			.and(place.deletedAt.isNull())
			.and(getAddressFilter(cond))
			.and(getKeywordFilter(cond.getKeyword()));

		// 거리 계산 (Address의 위경도 기준, km 단위). 위치 미제공 시 null
		NumberExpression<Double> distanceExpr = null;
		if (cond.getLat() != null && cond.getLng() != null) {
			distanceExpr = getDistanceExpression(cond.getLat(), cond.getLng(), address.lat, address.lng).divide(
				1000.0); // km 단위
		}

		NumberExpression<Long> proofCountExpr = getProofCountExpr();

		// 본문 쿼리
		JPAQuery<Tuple> query = queryFactory
			.select(
				place.id,
				place.title,
				address,
				address.lat,
				address.lng,
				place.viewCount,
				writer.id,
				writer.nickname,
				writer.level,
				writer.title.name,
				file.fileId,
				file.filePath,
				distanceExpr != null ? distanceExpr.as("distance") : Expressions.nullExpression(Double.class),
				proofCountExpr.as("proofCount")
			)
			.from(place)
			.join(place.user, writer)
			.leftJoin(writer.title, title)
			.leftJoin(address).on(place.addressId.eq(address.id))
			.leftJoin(placeFile).on(placeFile.place.eq(place))
			.leftJoin(file).on(file.eq(placeFile.file),
							   file.fileId.eq(JPAExpressions.select(subFile.fileId)
															.from(subFile)
															.join(placeFile).on(placeFile.file.eq(subFile))
															.where(placeFile.place.eq(place), subFile.deleted.isFalse())
															.orderBy(subFile.orderIdx.asc())
															.limit(1)))
			.where(where);

		// 정렬 조건 적용
		Pageable pageable = cond.getPageable();
		applySort(query, pageable, distanceExpr, proofCountExpr);

		// 카운트 쿼리
		Long total = Optional.ofNullable(queryFactory
											 .select(place.count())
											 .from(place)
											 .join(place.user, writer)
											 .leftJoin(address).on(place.addressId.eq(address.id))
											 .where(where)
											 .fetchOne()).orElse(0L);

		// 페이징 처리
		List<Tuple> tuples = query.offset(pageable.getOffset())
								  .limit(pageable.getPageSize())
								  .fetch();

		// 결과 매핑
		List<PlaceSummary> content = tuples.stream()
										   .map(PlaceSummaryMapper::fromTuple)
										   .toList();

		return PageResponse.toDto(new PageImpl<>(content, pageable, total));
	}

	// 2-1) ES → DB 상세: ids로 얇게 조회
	@Override
	@Transactional(readOnly = true)
	public List<PlaceSummary> findSummariesByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) return List.of();

		QPlaceFile placeFile = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QUser writer = QUser.user;
		QAttachmentFile subFile = new QAttachmentFile("sf");
		QTitle title = new QTitle("title");

		Expression<Double> distanceSel =
			ExpressionUtils.as(Expressions.nullExpression(Double.class), "distance");

		Expression<Long> proofCountSel =
			ExpressionUtils.as(Expressions.nullExpression(Long.class), "proofCount");

		List<Tuple> tuples = queryFactory
			.select(
				place.id,
				place.title,
				address,
				address.lat,
				address.lng,
				place.viewCount,
				writer.id,
				writer.nickname,
				writer.level,
				writer.title.name,
				file.fileId,
				file.filePath,
				distanceSel,
				proofCountSel
			)
			.from(place)
			.join(place.user, writer)
			.leftJoin(writer.title, title)
			.leftJoin(address).on(place.addressId.eq(address.id))
			.leftJoin(placeFile).on(placeFile.place.eq(place))
			.leftJoin(file).on(
				file.eq(placeFile.file),
				file.fileId.eq(JPAExpressions
								   .select(subFile.fileId)
								   .from(subFile)
								   .join(placeFile).on(placeFile.file.eq(subFile))
								   .where(placeFile.place.eq(place), subFile.deleted.isFalse())
								   .orderBy(subFile.orderIdx.asc())
								   .limit(1))
			)
			.where(
				place.deletedAt.isNull(),
				place.id.in(ids)
			)
			.fetch();

		return tuples.stream().map(PlaceSummaryMapper::fromTuple).toList();
	}

	// 2-2) 키워드 없음: ID만 페이지로 (필요 시 정렬 정책 보강)
	@Override
	@Transactional(readOnly = true)
	public List<Long> findPlaceIdsForSummaryWithoutKeyword(String emdCd, Pageable pageable) {
		BooleanBuilder where = new BooleanBuilder()
			.and(place.deletedAt.isNull());

		if (emdCd != null && !emdCd.isBlank()) {
			where.and(address.emdCd.eq(emdCd));
		}

		// 정렬: pageable이 주는 정렬을 해석 (기본 viewCount desc, createdAt desc)
		List<OrderSpecifier<?>> orders = new ArrayList<>();
		if (pageable.getSort().isSorted()) {
			for (Sort.Order o : pageable.getSort()) {
				boolean asc = o.isAscending();
				switch (o.getProperty()) {
					case "viewCount" -> orders.add(asc ? place.viewCount.asc() : place.viewCount.desc());
					case "createdAt" -> orders.add(asc ? place.createdAt.asc() : place.createdAt.desc());
					default -> {} // 무시
				}
			}
		}
		if (orders.isEmpty()) {
			orders.add(place.viewCount.desc());
			orders.add(place.createdAt.desc());
		}

		return queryFactory
			.select(place.id)
			.from(place)
			.leftJoin(address).on(place.addressId.eq(address.id))
			.where(where)
			.orderBy(orders.toArray(new OrderSpecifier<?>[0]))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	// 2-3) 키워드 없음: total 카운트
	@Override
	@Transactional(readOnly = true)
	public long countForSummaryWithoutKeyword(String emdCd) {
		BooleanBuilder where = new BooleanBuilder()
			.and(place.deletedAt.isNull());

		if (emdCd != null && !emdCd.isBlank()) {
			where.and(address.emdCd.eq(emdCd));
		}

		Long total = queryFactory
			.select(place.count())
			.from(place)
			.leftJoin(address).on(place.addressId.eq(address.id))
			.where(where)
			.fetchOne();

		return total == null ? 0L : total;
	}


	@Override
	public List<Tuple> findWithinRadius(double lat, double lon, double minKm, double maxKm) {
		// 1) 기준점 <-> 장소 간 거리를 미터 단위로 계산
		NumberExpression<Double> distMeter = Expressions.numberTemplate(
			Double.class,
			"ST_Distance_Sphere(POINT({1}, {0}), POINT({3}, {2}))",
			lat, lon,                      // {0}=lat, {1}=lon
			address.lat, address.lng       // {2}=place.lat, {3}=place.lng
		);

		// 2) 필터용 경계값 (km -> m 단위)
		double minMeters = minKm * 1_000;
		double maxMeters = maxKm * 1_000;

		return queryFactory
			.select(
				place.id,
				distMeter.divide(1_000.0)  // km 단위로 변환
			)
			.from(place)
			.leftJoin(address).on(place.addressId.eq(address.id))
			.where(
				place.deletedAt.isNull(),
				address.lat.isNotNull(),
				address.lng.isNotNull(),
				distMeter.between(minMeters, maxMeters))
			.fetch();
	}

	private void applySort(JPAQuery<Tuple> query, Pageable pageable,
						   NumberExpression<Double> distanceExpr, NumberExpression<Long> proofCountExpr) {
		PathBuilder<Place> entityPath = new PathBuilder<>(Place.class, "place");
		Set<String> allowedSortFields = Set.of("viewCount", "createdAt", "updatedAt");

		for (Sort.Order order : pageable.getSort()) {
			String property = order.getProperty();
			boolean asc = order.isAscending();

			switch (property) {
				case "distance" -> {
					if (distanceExpr != null)
						query.orderBy(asc ? distanceExpr.asc() : distanceExpr.desc());
				}
				case "proofCount" -> {
					if (proofCountExpr != null)
						query.orderBy(asc ? proofCountExpr.asc() : proofCountExpr.desc());
				}
				default -> {
					if (!allowedSortFields.contains(property)) continue;
					query.orderBy(new OrderSpecifier<>(
						asc ? Order.ASC : Order.DESC,
						entityPath.getComparable(property, Comparable.class)
					));
				}
			}
		}

		query.orderBy(place.id.asc());
	}

	/** 주소 필터: emd > sgg > sd (하나만 적용) */
	private BooleanExpression getAddressFilter(PlaceSearchCond cond) {
		if (cond.getEmdCd() != null && !cond.getEmdCd().isBlank()) {
			return address.emdCd.eq(cond.getEmdCd());
		}
		if (cond.getSggCd() != null && !cond.getSggCd().isBlank()) {
			return address.sggCd.eq(cond.getSggCd());
		}
		if (cond.getSdCd() != null && !cond.getSdCd().isBlank()) {
			return address.sdCd.eq(cond.getSdCd());
		}
		return null;
	}

	private BooleanExpression getKeywordFilter(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return place.title.containsIgnoreCase(keyword)
						  .or(address.detail.containsIgnoreCase(keyword));
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

	private NumberExpression<Long> getProofCountExpr() {
		QMissionProof mp = QMissionProof.missionProof;

		return Expressions.numberTemplate(Long.class,
										  "({0})",
										  JPAExpressions
											  .select(mp.count())
											  .from(mp)
											  .where(mp.place.eq(place), mp.deletedAt.isNull())
		);
	}
}
