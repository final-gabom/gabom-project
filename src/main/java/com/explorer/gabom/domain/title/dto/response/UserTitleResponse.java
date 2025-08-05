package com.explorer.gabom.domain.title.dto.response;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserTitleResponse {
	private Long id;
	private String name;
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
