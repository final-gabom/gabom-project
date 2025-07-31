package com.explorer.gabom.domain.user.dto;

import com.explorer.gabom.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {
	private Long id;
	private String nickname;
	private Integer level;
	private String title;

	public static UserSummaryDto toDto(User user) {
		return UserSummaryDto.builder()
							 .id(user.getId())
							 .nickname(user.getNickname())
							 .level(user.getLevel())
							 .title(user.getTitle().getName())
							 .build();
	}
}
