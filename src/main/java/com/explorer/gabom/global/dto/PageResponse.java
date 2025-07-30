package com.explorer.gabom.global.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse<T> {

	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public static <T> PageResponse<T> toDto(Page<T> pageData) {
		return PageResponse.<T>builder()
						   .content(pageData.getContent())
						   .page(pageData.getNumber())
						   .size(pageData.getSize())
						   .totalElements(pageData.getTotalElements())
						   .totalPages(pageData.getTotalPages())
						   .build();
	}
}
