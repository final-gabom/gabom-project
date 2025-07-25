package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.title.entity.Title;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleUpdateResponse {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;

	public static TitleUpdateResponse toDto(Title title) {
		return new TitleUpdateResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			title.getCreatedAt()
		);
	}
}
