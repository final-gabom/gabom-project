package com.explorer.gabom.domain.elasticsearch.service;

import java.io.IOException;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceSearchFacade {

	private final PlaceSearchService esSearchService; // ES에서 ID만 검색
	private final PlaceRepository placeRepository;    // Custom 포함

	/**
	 * 검색어가 있으면: ES -> ID, DB 상세, ES 순서 보존
	 * 검색어가 없으면: DB 경로 그대로 (emdCd 필터 + 정렬/페이징)
	 */
	@Transactional(readOnly = true)
	public PageResponse<PlaceSummary> search(
		String keyword,
		String emdCd,
		Double lat,
		Double lng,
		int page,
		int size
	) {
		try {

			Pageable pageable = PageRequest.of(page, size);

			// 1) 키워드 없으면 DB 경로
			if (!StringUtils.hasText(keyword)) {
				List<Long> ids = placeRepository.findPlaceIdsForSummaryWithoutKeyword(emdCd, pageable);
				if (ids.isEmpty()) {
					return PageResponse.toDto(Page.empty(pageable));
				}
				List<PlaceSummary> rows = placeRepository.findSummariesByIds(ids);
				long total = placeRepository.countForSummaryWithoutKeyword(emdCd);
				return PageResponse.toDto(new PageImpl<>(rows, pageable, total));
			}

			// 2) 키워드 있으면 ES -> ID
			PlaceSearchService.IdPage idPage = esSearchService.searchIds(
				keyword, emdCd, lat, lng,
				page * size, size
			);

			List<Long> ids = idPage.ids();
			if (ids == null || ids.isEmpty()) {
				return PageResponse.toDto(new PageImpl<>(Collections.emptyList(), pageable, 0));
			}

			// 3) DB 상세(얇게)
			List<PlaceSummary> rows = placeRepository.findSummariesByIds(ids);

			// 4) ES 순서 보존
			Map<Long, Integer> order = new HashMap<>(ids.size() * 2);
			for (int i = 0; i < ids.size(); i++)
				order.put(ids.get(i), i);
			rows.sort(Comparator.comparingInt(ps -> order.getOrDefault(ps.getPlaceId(), Integer.MAX_VALUE)));

			// 5) 응답 (total은 ES total)
			return PageResponse.toDto(new PageImpl<>(rows, pageable, idPage.total()));

		} catch (IOException e) {
			throw new IllegalStateException("ElasticSearch 검색 중 오류", e);
		}
	}
}
