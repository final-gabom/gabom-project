package com.explorer.gabom.domain.place.repository;

import static com.explorer.gabom.domain.address.entity.QAddress.*;
import static com.explorer.gabom.domain.place.entity.QPlace.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.querydsl.core.Tuple;
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
		boolean byPopularity = containsSortField(cond, "popularity"); // viewCount + proofCount

		BooleanExpression addrFilter = getAddressFilter(cond);
		boolean needAddressJoin = addrFilter != null || byDistance || keywordTouchesAddress(cond);

		JPAQuery<Long> q = queryFactory
			.select(place.id)
			.from(place)
			.where(place.deletedAt.isNull(), getKeywordFilter(cond.getKeyword()));

		if (needAddressJoin) {
			q.join(address).on(place.addressId.eq(address.id)).where(addrFilter);
		}

		NumberExpression<Double> distanceExpr = null;
		if (byDistance && cond.getLat() != null && cond.getLng() != null) {
			distanceExpr = getDistanceExpression(cond.getLat(), cond.getLng(), address.lat, address.lng)
				.divide(1000.0);
		}

		NumberExpression<Long> proofCountExpr = null;
		if (byPopularity) {
			QMissionProof mp = QMissionProof.missionProof;
			q.leftJoin(mp).on(mp.place.eq(place), mp.deletedAt.isNull());
			proofCountExpr = mp.id.countDistinct();
			q.groupBy(place.id);
		}

		applySortForThreeModes(q, cond.getPageable(), distanceExpr, proofCountExpr);

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

		// ---------- A) 미션 수 별도 집계 ----------
		QMissionProof mp = QMissionProof.missionProof;
		Map<Long, Integer> proofCountMap = queryFactory
			.select(mp.place.id, mp.id.countDistinct())
			.from(mp)
			.where(mp.deletedAt.isNull(), mp.place.id.in(placeIds))
			.groupBy(mp.place.id)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				t -> t.get(mp.place.id),
				t -> Objects.requireNonNull(t.get(mp.id.countDistinct())).intValue()
			));
		placeIds.forEach(id -> proofCountMap.putIfAbsent(id, 0));

		// ---------- B) 썸네일: (place_id, MIN(order_idx)) ----------
		QPlaceFile pf = QPlaceFile.placeFile;
		QAttachmentFile file = QAttachmentFile.attachmentFile;

		var minOrderSub = JPAExpressions
			.select(pf.orderIdx.min())
			.from(pf)
			.where(pf.place.id.eq(place.id));

		// ---------- C) 본문 SELECT ----------
		QUser writer = QUser.user;
		QTitle title = QTitle.title;

		QAddress address = QAddress.address;
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

		// ---------- D) 정렬 (최신 / 거리 / 인기 + placeIds 순서 유지) ----------
		applySortForThreeModes(query, cond.getPageable(), distanceExpr, null);

		// placeIds 순서 강제 (MySQL FIELD)
		String fieldOrder = placeIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		NumberExpression<Integer> orderByIds = Expressions.numberTemplate(
			Integer.class,
			"FIELD({0}, " + fieldOrder + ")",
			place.id
		);

		query.orderBy(new OrderSpecifier<>(Order.ASC, orderByIds));

		// ---------- E) 실행 ----------
		List<Tuple> tuples = query
			.offset(cond.getPageable().getOffset())
			.limit(cond.getPageable().getPageSize())
			.fetch();

		Long total = Optional.ofNullable(queryFactory
											 .select(place.count())
											 .from(place)
											 .where(place.deletedAt.isNull(), place.id.in(placeIds))
											 .fetchOne()).orElse(0L);

		// ---------- F) DTO 매핑 ----------
		List<PlaceSummary> content = tuples.stream().map(t -> PlaceSummaryMapper.fromTuple(t, proofCountMap)).toList();

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

	/** 정렬 3종 적용 */
	private <T> void applySortForThreeModes(JPAQuery<T> query, Pageable pageable,
											NumberExpression<Double> distanceExpr,
											NumberExpression<Long> proofCountExpr) {

		for (Sort.Order o : pageable.getSort()) {
			String prop = o.getProperty();
			boolean asc = o.isAscending();

			switch (prop) {
				case "createdAt" -> query.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, place.createdAt));
				case "distance" -> {
					if (distanceExpr != null) {
						query.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, distanceExpr));
					}
				}
				case "popularity" -> {
					// popularity = viewCount + proofCount
					NumberExpression<Long> vc = place.viewCount.coalesce(0).castToNum(Long.class);
					NumberExpression<Long> pc =
						(proofCountExpr != null)
						? proofCountExpr.coalesce(0L)
						: Expressions.numberTemplate(Long.class, "0");
					NumberExpression<Long> popularity = vc.add(pc);
					query.orderBy(new OrderSpecifier<>(asc ? Order.ASC : Order.DESC, popularity));
				}
				default -> { /* no-op */ }
			}
		}
		// tie-breaker
		query.orderBy(place.id.desc());
	}

	private boolean keywordTouchesAddress(PlaceSearchCond cond) {
		String kw = cond.getKeyword();
		return kw != null && !kw.isBlank();
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
