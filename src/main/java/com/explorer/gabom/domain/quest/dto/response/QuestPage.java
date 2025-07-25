package com.explorer.gabom.domain.quest.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.explorer.gabom.domain.quest.dto.QuestDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestPage {

	private List<QuestDto> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public static QuestPage toDto(Page<QuestDto> pagedata) {
		return QuestPage.builder()
						.content(pagedata.getContent())
						.page(pagedata.getNumber())
						.size(pagedata.getSize())
						.totalElements(pagedata.getTotalElements())
						.totalPages(pagedata.getTotalPages())
						.build();
	}

}
