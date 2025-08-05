package com.explorer.gabom.domain.title.dto;

import com.explorer.gabom.domain.title.entity.Title;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TitleDto {

	@Schema(description = "칭호 ID")
	private final Long id;
	@Schema(description = "칭호 이름")
	private final String name;
	@Schema(description = "칭호 설명")
	private final String description;

	public static TitleDto toDto(Title title) {
		return TitleDto.builder()
					   .id(title.getId())
					   .name(title.getName())
					   .description(title.getDescription())
					   .build();
	}
}
