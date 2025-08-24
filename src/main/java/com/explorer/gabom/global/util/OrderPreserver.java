package com.explorer.gabom.global.util;

import java.util.*;
import java.util.function.Function;

public final class OrderPreserver {

	private OrderPreserver() {}

	/** refOrder의 순서를 기준으로 items를 재정렬한다. (키 추출 함수 필요) */
	public static <T, K> List<T> reorderLike(List<T> items, List<K> refOrder, Function<T, K> keyFn) {
		if (items == null || items.isEmpty() || refOrder == null || refOrder.isEmpty()) return items;
		Map<K, Integer> pos = new HashMap<>(refOrder.size() * 2);
		for (int i = 0; i < refOrder.size(); i++) pos.put(refOrder.get(i), i);

		items.sort(Comparator.comparingInt(o -> pos.getOrDefault(keyFn.apply(o), Integer.MAX_VALUE)));
		return items;
	}
}
