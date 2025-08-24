package com.explorer.gabom.domain.place.repository;

import static com.explorer.gabom.domain.address.entity.QAddress.*;
import static com.explorer.gabom.domain.place.entity.QPlace.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
		boolean byDistance = containsSortField(cond, "distance");
		boolean byPopularity = containsSortField(cond, "popularity");
		boolean byRating = containsSortField(cond, "rating");

		BooleanExpression addrFilter = getAddressFilter(cond);
		BooleanExpression keywordFilter = getKeywordFilter(cond.getKeyword());

		// 주소 조인이 필요한 경우: 행정코드 필터 or 위치(boundingBox) or 키워드(주소 검색)
		boolean needAddressJoin = (addrFilter != null) || cond.hasLatLng() || (keywordFilter != null);

		JPAQuery<Long> q = queryFactory
			.select(place.id)
			.from(place)
			.where(place.deletedAt.isNull());

		if (needAddressJoin) {
			q.join(address).on(place.addressId.eq(address.id));
			if (addrFilter != null) {
				q.where(addrFilter);
			}
		}

		// 키워드 필터는 address.detail을 참조하므로, 주소 조인 이후에 적용
		if (keywordFilter != null) {
			q.where(keywordFilter);
		}

		// 위치가 있으면 BBOX 적용 (where는 BooleanExpression만)
		NumberExpression<Double> distanceExpr = null;
		if (cond.hasLatLng()) {
			double radiusKm = (cond.getRadiusKm() != null) ? cond.getRadiusKm() : 10.0;
			q.where(boundingBox(cond.getLat(), cond.getLng(), radiusKm, address.lat, address.lng));

			// distance 정렬일 때만 실제 거리식(숫자식) 준비 (정렬에서만 사용)
			if (byDistance) {
				distanceExpr = getDistanceExpression(cond.getLat(), cond.getLng(), address.lat, address.lng)
					.divide(1000.0);
			}
		}

		QMissionProof mp = QMissionProof.missionProof;

		if (byPopularity || byRating) {
			q.leftJoin(mp).on(mp.place.eq(place), mp.deletedAt.isNull());
			q.groupBy(place.id);

			NumberExpression<Double> avgRating   = mp.starRating.avg().coalesce(0.0);
			NumberExpression<Long>   proofCount  = mp.id.count().coalesce(0L);

			// 여기서 통일된 정렬 적용
			applySortSafe(q, cond.getPageable(), distanceExpr, proofCount, avgRating);
		}

		// 페이징
		return q.offset(cond.getPageable().getOffset())
				.limit(cond.getPageable().getPageSize())
				.fetch();
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<PlaceSummary> fetchPlaceSummariesByIds(List<Long> placeIds, PlaceSearchCond cond) {
		if (placeIds == null || placeIds.isEmpty()) {
			return PageResponse.toDto(Page.empty(cond.getPageable()));
		}

		// ---------- A) 미션수 & 평균 평점 집계 ----------
		QMissionProof mp = QMissionProof.missionProof;
		var cntExpr = mp.id.count();
		var avgExpr = mp.starRating.avg();

		List<Tuple> stats = queryFactory
			.select(mp.place.id, cntExpr, avgExpr)
			.from(mp)
			.where(mp.deletedAt.isNull(), mp.place.id.in(placeIds))
			.groupBy(mp.place.id)
			.fetch();

		Map<Long, Integer> proofCountMap = new HashMap<>(placeIds.size() * 2);
		Map<Long, Double> avgRatingMap = new HashMap<>(placeIds.size() * 2);
		for (Tuple t : stats) {
			Long pid = t.get(mp.place.id);
			proofCountMap.put(pid, Optional.ofNullable(t.get(cntExpr)).map(Long::intValue).orElse(0));
			avgRatingMap.put(pid, Optional.ofNullable(t.get(avgExpr)).orElse(0.0));
		}
		placeIds.forEach(id -> {
			proofCountMap.putIfAbsent(id, 0);
			avgRatingMap.putIfAbsent(id, 0.0);
		});

		// ---------- B) 썸네일 서브쿼리 ----------
		QPlaceFile pf = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		var minOrderSub = JPAExpressions
			.select(pf.orderIdx.min())
			.from(pf)
			.where(pf.place.id.eq(place.id));

		// ---------- C) 본문 SELECT (정렬 제거) ----------
		QUser writer = QUser.user;
		QTitle title = QTitle.title;
		QAddress address = QAddress.address;

		// 거리 표현식은 그대로(표시용) 유지하되, 정렬에는 사용하지 않음
		NumberExpression<Double> distanceExpr = getOptionalDistanceExpr(cond, address);

		JPAQuery<Tuple> query = queryFactory
			.select(
				place.id, place.title,
				address.id, address.sdCd, address.sggCd, address.emdCd, address.detail, address.lat, address.lng,
				place.viewCount,
				writer.id, writer.nickname, writer.level, title.name,
				file.fileId, file.filePath,
				distanceExpr == null ? Expressions.constant(0.0) : distanceExpr.as("distance")
			)
			.from(place)
			.join(place.user, writer)
			.leftJoin(writer.title, title)
			.join(address).on(place.addressId.eq(address.id))
			.leftJoin(pf).on(pf.place.eq(place), pf.orderIdx.eq(minOrderSub))
			.leftJoin(file).on(file.eq(pf.file))
			.where(place.deletedAt.isNull(), place.id.in(placeIds));

		// ---------- D) 순서 유지: FIELD 한 번만 ----------
		String fieldOrder = placeIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		var orderByIds = Expressions.numberTemplate(Integer.class, "FIELD({0}, " + fieldOrder + ")", place.id);
		query.orderBy(new OrderSpecifier<>(Order.ASC, orderByIds));

		// ---------- E) 실행 ----------
		List<Tuple> tuples = query
			.offset(cond.getPageable().getOffset())
			.limit(cond.getPageable().getPageSize())
			.fetch();

		Long total = Optional.ofNullable(
			queryFactory.select(place.count())
						.from(place)
						.where(place.deletedAt.isNull(), place.id.in(placeIds))
						.fetchOne()
		).orElse(0L);

		// ---------- F) DTO 매핑 ----------
		List<PlaceSummary> content = tuples.stream()
										   .map(t -> PlaceSummaryMapper.fromTuple(t, proofCountMap, avgRatingMap))
										   .toList();

		return PageResponse.toDto(new PageImpl<>(content, cond.getPageable(), total));
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

	private BooleanExpression boundingBox(double lat, double lng, double km, NumberPath<Double> tLat,
										  NumberPath<Double> tLng) {
		// 대략적 변환(위도 1도 ≈ 111km, 경도는 위도에 따라 변동)
		double latDelta = km / 111.0;
		double lngDelta = km / (111.0 * Math.cos(Math.toRadians(lat)));
		double minLat = lat - latDelta, maxLat = lat + latDelta;
		double minLng = lng - lngDelta, maxLng = lng + lngDelta;
		return tLat.between(minLat, maxLat).and(tLng.between(minLng, maxLng));
	}

	private <T> void applySortSafe(JPAQuery<T> q,
								   Pageable pageable,
								   NumberExpression<Double> distanceExpr,
								   NumberExpression<Long> proofCountExpr,
								   NumberExpression<Double> avgRatingExpr) {

		for (Sort.Order o : pageable.getSort()) {
			boolean asc = o.isAscending();
			String prop = o.getProperty();

			switch (prop) {
				case "createdAt" -> q.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, place.createdAt));

				case "distance" -> {
					if (distanceExpr != null) {
						// 그룹바이 문맥에서도 안전하게: ANY_VALUE(distance)
						var safe = Expressions.numberTemplate(Double.class, "ANY_VALUE({0})", distanceExpr);
						q.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, safe));
					}
				}

				case "popularity" -> {
					// popularity = viewCount + proofCount
					NumberExpression<Long> vc = place.viewCount.coalesce(0).castToNum(Long.class);
					NumberExpression<Long> pc = (proofCountExpr != null) ? proofCountExpr.coalesce(0L)
																		 : Expressions.numberTemplate(Long.class, "0");
					NumberExpression<Long> popularity = vc.add(pc);
					q.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, popularity));
				}

				case "rating" -> {
					if (avgRatingExpr != null) {
						q.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, avgRatingExpr.coalesce(0.0)));
					}
				}

				default -> { /* no-op */ }
			}
		}
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
