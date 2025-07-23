package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.title.entity.Title;

public record TitleResponse (
	Long id,
	String name,
	String description,
	LocalDateTime createdAt
	) {
	public static TitleResponse from(Title title) {
		return new TitleResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			title.getCreatedAt()
		);
	}
}
