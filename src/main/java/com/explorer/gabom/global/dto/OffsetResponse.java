package com.explorer.gabom.global.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OffsetResponse<T> {
	private List<T> content;
	private int size;
	private Long lastId;
	private long totalElements;
}
