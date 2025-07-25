package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleUpdateResponse implements TargetIdentifiable {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime updatedAt;

	public static TitleUpdateResponse toDto(Title title) {
		return new TitleUpdateResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			title.getUpdatedAt()
		);
	}

	@Override
	public Long getTargetId() {
		return id;
	}
}
