package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.title.entity.Title;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCreateResponse {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;

	public static TitleCreateResponse from(Title title) {
		return new TitleCreateResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			title.getCreatedAt()
		);
	}
}
