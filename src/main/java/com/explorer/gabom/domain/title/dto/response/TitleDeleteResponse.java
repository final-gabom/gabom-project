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
public class TitleDeleteResponse implements TargetIdentifiable {
	private Long id;
	private LocalDateTime deletedAt;

	public static TitleDeleteResponse toDto(Title title, LocalDateTime deletedAt) {
		return new TitleDeleteResponse(
			title.getId(),
			deletedAt
		);
	}
	@Override
	public Long getTargetId() {
		return id;
	}
}
