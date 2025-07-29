package com.explorer.gabom.domain.title.repository;

import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.title.entity.QTitle;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TitleRepositoryCustomImpl implements TitleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public void updateTitle(Long titleId, String name, String description) {
		QTitle title = QTitle.title;

		JPAUpdateClause updateQuery = queryFactory.update(title)
												  .where(title.id.eq(titleId));

		if (name != null) {
			updateQuery.set(title.name, name);
		}
		if (description != null) {
			updateQuery.set(title.description, description);
		}
		updateQuery.execute();
	}
}
