package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.entity.QMissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MissionProofRepositoryImpl implements MissionProofRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<MissionProof> searchMissionProofs(ListMissionProofRequest request, Pageable pageable) {
		QMissionProof missionProof = QMissionProof.missionProof;
		BooleanBuilder builder = new BooleanBuilder();

		if (request.getType() != null) {
			builder.and(missionProof.fieldType.eq(request.getType()));
		}
		if (request.getTargetId() != null) {
			builder.and(missionProof.targetId.eq(request.getTargetId()));
		}
		if (request.getUserId() != null) {
			builder.and(missionProof.user.id.eq(request.getUserId()));
		}
		builder.and(missionProof.deletedAt.isNull());

		JPAQuery<MissionProof> query = queryFactory
			.selectFrom(missionProof)
			.leftJoin(missionProof.user).fetchJoin()
			.where(builder);

		// 정렬 처리
		for (Sort.Order order : pageable.getSort()) {
			PathBuilder<MissionProof> pathBuilder = new PathBuilder<>(MissionProof.class, "missionProof");
			query.orderBy(new OrderSpecifier<>(
				order.isAscending() ? Order.ASC : Order.DESC,
				pathBuilder.getComparable(order.getProperty(), Comparable.class)
			));
		}

		// 페이징 처리
		List<MissionProof> content = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(missionProof.count())
			.from(missionProof)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total);
	}
}