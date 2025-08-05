package com.explorer.gabom.domain.title.dto.response;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleDeleteResponse implements TargetIdentifiable {
	@Schema(description = "삭제된 칭호 ID")
	private Long id;

	public static TitleDeleteResponse toDto(Title title) {
		return new TitleDeleteResponse(title.getId());
	}
	@Override
	public Long getTargetId() {
		return id;
	}
}
