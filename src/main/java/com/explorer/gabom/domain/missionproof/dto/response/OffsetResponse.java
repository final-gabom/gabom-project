package com.explorer.gabom.domain.missionproof.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OffsetResponse<T> {
	private final List<T> items;
	private final int size;
	private final Long lastId;
	private final long totalElements;
}
