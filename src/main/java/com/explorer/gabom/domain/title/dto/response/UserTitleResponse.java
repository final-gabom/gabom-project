package com.explorer.gabom.domain.title.dto.response;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserTitleResponse implements TargetIdentifiable {
	private Long id;
	private String name;
	private String description;
	private boolean isEquipped;

	public static UserTitleResponse toDto(UserTitle userTitle) {
		Title title = userTitle.getTitle();

		return new UserTitleResponse(
			title.getId(),
			title.getName(),
			title.getDescription(),
			userTitle.isEquipped()
		);
	}

	@Override
	public Long getTargetId() {
		return id;
	}
}
