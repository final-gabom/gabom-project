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
public class TitleCreateResponse implements TargetIdentifiable {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;

	public static TitleCreateResponse toDto(Title title) {
		return new TitleCreateResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			title.getCreatedAt()
			);
	}

	@Override
	public Long getTargetId() {
		return id;
	}


}
