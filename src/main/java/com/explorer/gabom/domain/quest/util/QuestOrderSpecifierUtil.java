package com.explorer.gabom.domain.quest.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.explorer.gabom.domain.quest.entity.QQuest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

public class QuestOrderSpecifierUtil {

	private static final QQuest quest = QQuest.quest;

	public static List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();
		for (Sort.Order order : pageable.getSort()) {
			Order direction = order.isAscending() ? Order.ASC : Order.DESC;
			String property = order.getProperty();

			switch (property) {
				case "id":
					orders.add(new OrderSpecifier<>(direction, quest.id));
					break;
				case "title":
					orders.add(new OrderSpecifier<>(direction, quest.title));
					break;
				case "rewardPoint":
					orders.add(new OrderSpecifier<>(direction, quest.rewardPoint));
					break;
				case "acquireCondition":
					orders.add(new OrderSpecifier<>(direction, quest.acquireCondition));
					break;
				default:
					break;
			}
		}
		if (orders.isEmpty()) {
			orders.add(new OrderSpecifier<>(Order.ASC, quest.id));
		}
		return orders;
	}
}
