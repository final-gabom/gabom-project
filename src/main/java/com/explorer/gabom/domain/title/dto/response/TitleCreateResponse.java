package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCreateResponse implements TargetIdentifiable {
	@Schema(description = "등록된 칭호 ID")
	private Long id;
	@Schema(description = "등록된 칭호 이름")
	private String name;
	@Schema(description = "등록된 칭호 설명")
	private String description;
	@Schema(description = "등록일")
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
