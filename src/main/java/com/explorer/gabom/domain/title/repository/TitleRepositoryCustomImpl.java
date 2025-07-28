package com.explorer.gabom.domain.title.repository;

import org.springframework.stereotype.Repository;

import com.explorer.gabom.domain.title.entity.QTitle;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TitleRepositoryCustomImpl implements TitleRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public void updateTitle(Long titleId, String name,  String description) {
		QTitle title = QTitle.title;

		var update = queryFactory.update(title)
								 .where(title.id.eq(titleId));

		if (name != null) {
			update.set(title.name, name);
		}
		if (description != null) {
			update.set(title.description, description);
		}
		update.execute();
	}
}
