package com.explorer.gabom.domain.quest.repository;

import static com.explorer.gabom.domain.quest.util.QuestOrderSpecifierUtil.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.quest.entity.QUserQuest;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQuestRepositoryImpl implements UserQuestRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

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

	@Override
	public void bulkUpdateUserQuestStatusByQuest(Quest quest) {
		LocalDateTime now = LocalDateTime.now();

		queryFactory.update(uq)
					.set(uq.progressStatus, ProgressStatus.COMPLETED)
					.set(uq.completedAt, now)
					.where(
						uq.quest.eq(quest),
						uq.deleted.isFalse(),
						uq.progressCount.goe(quest.getAcquireCondition())
					)
					.execute();

		queryFactory.update(uq)
					.set(uq.progressStatus, ProgressStatus.IN_PROGRESS)
					.set(uq.completedAt, (LocalDateTime)null)
					.where(
						uq.quest.eq(quest),
						uq.deleted.isFalse(),
						uq.progressCount.lt(quest.getAcquireCondition())
					)
					.execute();

		em.clear();
	}

	@Override
	public void bulkDeleteByQuest(Quest quest) {
		LocalDateTime now = LocalDateTime.now();

		queryFactory.update(uq)
					.set(uq.deleted, true)
					.set(uq.deletedAt, now)
					.where(uq.quest.eq(quest))
					.execute();

		em.clear();
	}

	private BooleanExpression statusEq(ProgressStatus status) {
		return status != null ? uq.progressStatus.eq(status) : null;
	}
}
