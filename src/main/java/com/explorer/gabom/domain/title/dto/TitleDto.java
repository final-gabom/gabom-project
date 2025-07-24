package com.explorer.gabom.domain.title.dto;

import com.explorer.gabom.domain.title.entity.Title;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TitleDto {

	private final Long id;
	private final String name;
	private final String description;

	public static TitleDto toDto(Title title) {
		return TitleDto.builder()
					   .id(title.getId())
					   .name(title.getName())
					   .description(title.getDescription())
					   .build();
	}
}
