package com.explorer.gabom.domain.ranking.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.explorer.gabom.domain.ranking.dto.RankingDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingPage {
	private List<RankingDto> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public static RankingPage toDto(Page<RankingDto> pageData) {
		return RankingPage.builder()
						  .content(pageData.getContent())
						  .page(pageData.getNumber())
						  .size(pageData.getSize())
						  .totalElements(pageData.getTotalElements())
						  .totalPages(pageData.getTotalPages())
						  .build();
	}
}
