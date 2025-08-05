package com.explorer.gabom.domain.quest.repository;

import static com.explorer.gabom.domain.quest.util.QuestOrderSpecifierUtil.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.quest.entity.QQuest;
import com.explorer.gabom.domain.quest.entity.QUserQuest;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryImpl implements QuestRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QQuest quest = QQuest.quest;
	QUserQuest userQuest = QUserQuest.userQuest;

	@Override
	public Page<Quest> findQuestsNotJoinedByUser(Long userId, Pageable pageable) {
		List<OrderSpecifier<?>> orders = getOrderSpecifiers(pageable);

		List<Quest> contents = queryFactory
			.selectFrom(quest)
			.where(
				quest.deleted.isFalse(),
				notExistsUserQuest(userId)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(orders.toArray(new OrderSpecifier[0]))
			.fetch();

		return PageableExecutionUtils.getPage(contents, pageable,
											  () -> queryFactory
												  .selectFrom(quest)
												  .where(
													  quest.deleted.isFalse(),
													  notExistsUserQuest(userId)
												  )
												  .fetch()
												  .size()
		);
	}

	private BooleanExpression notExistsUserQuest(Long userId) {
		return queryFactory
			.selectOne()
			.from(userQuest)
			.where(
				userQuest.user.id.eq(userId),
				userQuest.quest.id.eq(quest.id),
				userQuest.deleted.isFalse()
			)
			.notExists();
	}

	@Override
	public List<Quest> findByQuestConditionTypeAndDeletedFalse(QuestConditionType questConditionType) {
		return queryFactory
			.selectFrom(quest)
			.where(
				quest.questConditionType.eq(questConditionType),
				quest.deleted.isFalse()
			)
			.fetch();
	}
}
