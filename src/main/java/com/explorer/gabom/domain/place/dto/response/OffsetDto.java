package com.explorer.gabom.domain.place.dto.response;

import java.util.List;

public record OffsetDto<T>(
	List<T> content,
	Integer page,
	Integer size,
	Long totalElements,
	Integer totalPages,
	Boolean hasNext
) {}