package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.entity.QMissionProof;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MissionProofQueryRepositoryImpl implements MissionProofQueryRepository {

	private final JPAQueryFactory queryFactory;

	/**
	 * 미션 인증(MissionProof)을 검색 조건에 따라 조회하여 DTO로 반환하는 메서드
	 *
	 * @param condition 검색 조건
	 * @return MissionProofSummary 리스트 (요약 정보 DTO)
	 */
	@Override
	public Page<MissionProof> searchByCondition(MissionProofSearchCondition condition, Pageable pageable) {
		QMissionProof proof = QMissionProof.missionProof;
		QUser user = QUser.user;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QTitle title = QTitle.title;

		List<Tuple> tuples = queryFactory
			// 필요한 필드를 하나씩 지정 조회
			.select(
				proof.id,
				proof.fieldType,
				user.id,
				user.nickname,
				user.level,
				title.name,
				proof.title,
				proof.createdAt,
				proof.updatedAt,
				file.filePath  // 여기서 file.filePath로 하나씩 뽑음
			)
			.from(proof)
			.join(proof.user, user)
			.leftJoin(user.title, title)
			.leftJoin(proof.imageFiles, file)
			.where(buildWhereConditions(condition))
			.orderBy(proof.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 중복된 MissionProof ID별로 그룹화 (한 게시글에 여러 개의 이미지 파일이 있는 등의 경우 때무ㄴ에)
		Map<Long, List<Tuple>> groupedByProofId = tuples.stream()
														.collect(Collectors.groupingBy(tuple -> tuple.get(proof.id)));

		// 그룹된 Tuple을 MissionProof로 변환
		List<MissionProof> results = queryFactory
			.selectFrom(proof)
			.join(proof.user, user).fetchJoin()
			.leftJoin(proof.imageFiles, QAttachmentFile.attachmentFile).fetchJoin()
			.leftJoin(user.title, QTitle.title).fetchJoin()
			.where(buildWhereConditions(condition))
			.orderBy(proof.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.distinct()
			.fetch();

		long total = countByCondition(condition);
		return new PageImpl<>(results, pageable, total);
	}
	/**
	 * 검색 조건에 해당하는 전체 결과의 개수를 반환하는 메서드
	 *
	 * @param condition 검색 조건
	 * @return 개수 (long 타입)
	 */
	@Override
	public long countByCondition(MissionProofSearchCondition condition) {
		QMissionProof proof = QMissionProof.missionProof;

		return queryFactory
			.select(proof.count()) // count(*) 쿼리
			.from(proof)
			.where(
				buildWhereConditions(condition) // 동일한 검색 조건
			)
			.fetchOne(); // 단일 결과 (long)
	}

	/**
	 * 검색 조건을 기반으로 BooleanBuilder를 생성하여 동적 where 조건 생성
	 *
	 * @param condition 검색 조건 DTO
	 * @return Predicate (QueryDSL 조건 객체)
	 */
	private Predicate buildWhereConditions(MissionProofSearchCondition condition) {
		QMissionProof proof = QMissionProof.missionProof;
		BooleanBuilder builder = new BooleanBuilder();

		// 필드 타입 조건 (예: PLACE, EVENT 등)
		if (condition.hasTypeCondition()) {
			builder.and(proof.fieldType.eq(condition.getFieldType()));
			builder.and(proof.targetId.eq(condition.getTypeId()));
		}

		// 특정 사용자 조건이 있을 경우
		if (condition.hasUserCondition()) {
			builder.and(proof.user.id.eq(condition.getUserId()));
		}
		return builder;
	}
}

