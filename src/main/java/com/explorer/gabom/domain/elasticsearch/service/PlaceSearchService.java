package com.explorer.gabom.domain.elasticsearch.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

	private final ElasticsearchClient es;
	private static final String IDX = "places_v1";

	public IdPage searchIds(@Nullable String keyword,
							@Nullable String emdCd,
							@Nullable Double lat, @Nullable Double lon,
							int from, int size) throws IOException {

		SearchRequest.Builder sb = new SearchRequest.Builder()
			.index(IDX)
			.from(from)
			.size(size)
			.trackTotalHits(t -> t.enabled(true))          // 총건수 정확히 받기
			.source(sc -> sc.fetch(false))                 // _source 제외 → id만

			.query(qb -> qb.bool(b -> {
				if (keyword != null && !keyword.isBlank()) {
					b.should(sh -> sh.multiMatch(mm -> mm
						.query(keyword)
						.fields("title^3,address_full^2")));
					b.minimumShouldMatch("1");
				}
				if (emdCd != null && !emdCd.isBlank()) {
					b.filter(f -> f.term(t -> t.field("emdCd").value(emdCd)));
				}
				return b;
			}))

			// 정렬: 점수 → 인기 → 최신
			// (geo 정렬은 클라이언트 API가 버전별로 달라 실수 잦아서 일단 제외)
			.sort(so -> so.field(f -> f.field("_score").order(SortOrder.Desc)))
			.sort(so -> so.field(f -> f.field("popularity").order(SortOrder.Desc)))
			.sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc)));

		SearchResponse<Void> resp = es.search(sb.build(), Void.class);

		List<Long> ids = resp.hits().hits().stream()
							 .map(h -> Long.valueOf(h.id()))
							 .toList();

		long total = resp.hits().total() != null ? resp.hits().total().value() : ids.size();
		return new IdPage(ids, total);
	}

	public record IdPage(List<Long> ids, long total) {}
}
