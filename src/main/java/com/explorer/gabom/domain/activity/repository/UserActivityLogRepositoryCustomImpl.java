package com.explorer.gabom.domain.activity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.activity.entity.QUserActivityLog;
import com.explorer.gabom.domain.activity.entity.UserActivityLog;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserActivityLogRepositoryCustomImpl implements UserActivityLogRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<UserActivityLog> searchMyLogs(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
		QUserActivityLog log = QUserActivityLog.userActivityLog;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(log.userId.eq(userId));

		if (from != null) builder.and(log.createdAt.goe(from));
		if (to != null) builder.and(log.createdAt.loe(to));

		List<UserActivityLog> result = jpaQueryFactory
			.selectFrom(log)
			.where(builder)
			.orderBy(log.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(log.count())
			.from(log)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(result, pageable, total);
	}
}
