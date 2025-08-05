package com.explorer.gabom.domain.title.dto.response;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserTitleResponse {

	@Schema(description = "조회한 칭호 ID")
	private Long id;
	@Schema(description = "조회한 칭호 이름")
	private String name;
	@Schema(description = "조회한 칭호 설명")
	private String description;

	public static UserTitleResponse toDto(UserTitle userTitle) {
		Title title = userTitle.getTitle();
		if (title == null) {
			return null;
		}

		return new UserTitleResponse(
			title.getId(),
			title.getName(),
			title.getDescription()
		);
	}

}
