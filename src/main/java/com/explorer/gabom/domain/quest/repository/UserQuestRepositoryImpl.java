package com.explorer.gabom.domain.quest.repository;

import static com.explorer.gabom.domain.quest.util.QuestOrderSpecifierUtil.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.quest.entity.QUserQuest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQuestRepositoryImpl implements UserQuestRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QUserQuest uq = QUserQuest.userQuest;

	@Override
	public Optional<Integer> findMaxProgressByUserAndQuestType(Long userId, QuestConditionType type) {
		Integer result = queryFactory
			.select(uq.progressCount.max())
			.from(uq)
			.where(
				uq.user.id.eq(userId),
				uq.quest.questConditionType.eq(type),
				uq.deleted.isFalse()
			)
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public Page<UserQuest> findUserQuests(Long userId, ProgressStatus status, Pageable pageable) {
		List<UserQuest> contents = queryFactory
			.selectFrom(uq)
			.where(
				uq.user.id.eq(userId),
				uq.quest.deleted.isFalse(),
				statusEq(status)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
			.fetch();

		return PageableExecutionUtils.getPage(contents, pageable,
											  () -> queryFactory
												  .selectFrom(uq)
												  .where(
													  uq.user.id.eq(userId),
													  uq.quest.deleted.isFalse(),
													  statusEq(status)
												  )
												  .fetch()
												  .size()
		);
	}

	private BooleanExpression statusEq(ProgressStatus status) {
		return status != null ? uq.progressStatus.eq(status) : null;
	}
}
