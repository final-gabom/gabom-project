package com.explorer.gabom.domain.missionproof.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSearchCondition;
import com.explorer.gabom.domain.missionproof.dto.response.MissionProofSummary;
import com.explorer.gabom.domain.missionproof.entity.QMissionProof;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
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
	public List<MissionProofSummary> searchByCondition(MissionProofSearchCondition condition) {
		QMissionProof proof = QMissionProof.missionProof;
		QUser user = QUser.user;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		QTitle title = QTitle.title;

		return queryFactory
			// MissionProofSummary 생성자를 직접 호출하여 select
			.select(Projections.constructor(
				MissionProofSummary.class,
				proof.id, // 미션 인증 ID
				proof.fieldType, // 미션 필드 타입(enum 등)

				// 내부에 포함된 UserSummaryDto 생성자를 통해 사용자 요약 정보 매핑
				Projections.constructor(
					UserSummaryDto.class,
					user.id,           // 사용자 ID
					user.nickname,     // 닉네임
					user.level,        // 레벨
					title.name         // 칭호 이름 (leftJoin)
				),

				proof.title,       // 미션 제목
				proof.createdAt,   // 생성일
				proof.updatedAt,   // 수정일
				proof.imageFiles   // 첨부 이미지 리스트 (List<AttachmentFile>)
			))
			.from(proof) // from절 설정
			.join(proof.user, user) // 미션 작성자와 조인
			.leftJoin(user.title, title) // 사용자 칭호 정보는 left join
			.leftJoin(proof.imageFiles, file) // 이미지 파일도 left join
			.where(
				buildWhereConditions(condition) // 조건절 동적 생성
			)
			.orderBy(proof.id.desc()) // 최신 순 정렬 (cursor 기반 페이징에 필요)
			.limit(condition.getSize()) // 페이지 사이즈 제한
			.fetch(); // 결과 리스트 반환
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
			builder.and(proof.fieldType.eq(condition.getFieldType())); // fieldType 일치
			builder.and(proof.id.eq(condition.getTypeId())); // 해당 typeId에 해당하는 인증만
		}

		// 특정 사용자 조건이 있을 경우
		if (condition.hasUserCondition()) {
			builder.and(proof.user.id.eq(condition.getUserId())); // 사용자 ID 일치
		}

		// 커서 기반 페이징 처리: 마지막 ID보다 작은 데이터만 가져오기
		if (condition.isCursorPaging()) {
			builder.and(proof.id.lt(condition.getLastId()));
		}

		return builder;
	}
}

