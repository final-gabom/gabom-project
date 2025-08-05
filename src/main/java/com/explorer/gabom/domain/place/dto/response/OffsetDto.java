package com.explorer.gabom.domain.place.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이지네이션 응답 정보")
public record OffsetDto<T>(

	@Schema(description = "현재 페이지에 포함된 데이터 목록")
	List<T> content,

	@Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
	Integer page,

	@Schema(description = "페이지당 데이터 수", example = "10")
	Integer size,

	@Schema(description = "전체 데이터 개수", example = "132")
	Long totalElements,

	@Schema(description = "전체 페이지 수", example = "14")
	Integer totalPages,

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	Boolean hasNext

) {}