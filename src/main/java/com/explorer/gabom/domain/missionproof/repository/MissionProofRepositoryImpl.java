package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.missionproof.dto.request.ListMissionProofRequest;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.entity.QMissionProof;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MissionProofRepositoryImpl implements MissionProofRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<MissionProof> searchMissionProofs(ListMissionProofRequest request, int size) {
		QMissionProof missionProof = QMissionProof.missionProof;
		BooleanBuilder builder = new BooleanBuilder();

		// 인증 대상 (type: PLACE or QUEST)
		if (request.getType() != null) {
			builder.and(
				missionProof.fieldType.eq(request.getType())
			);
		}

		// 대상 ID
		if (request.getTargetId() != null) {
			builder.and(missionProof.targetId.eq(request.getTargetId()));
		}

		// 작성자 ID
		if (request.getUserId() != null) {
			builder.and(missionProof.user.id.eq(request.getUserId()));
		}

		// Soft Delete
		builder.and(missionProof.deletedAt.isNull());

		// 무한 스크롤 (Offset 방식)
		if (request.getLastId() != null) {
			builder.and(missionProof.id.lt(request.getLastId()));
		}

		// 정렬 (기본 최신순)
		OrderSpecifier<?> order = switch (request.getSort()) {
			case "oldest" -> missionProof.id.asc();
			default -> missionProof.id.desc(); // 최신순
		};

		return queryFactory
			.selectFrom(missionProof)
			.leftJoin(missionProof.user).fetchJoin()
			.where(builder)
			.orderBy(order)
			.limit(size)
			.fetch();
	}
}