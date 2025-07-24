package com.explorer.gabom.domain.user.dto;

import com.explorer.gabom.domain.title.dto.TitleDto;
import com.explorer.gabom.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {

	private final Long id;

	private final String nickname;

	private final String email;

	private final String profileImgUrl;

	private final TitleDto title;

	private final String address;

	private final Integer level;

	private final Integer exp;

	private final Integer point;

	public static UserDto toDto(User user) {
		return UserDto.builder().id(user.getId())
					  .nickname(user.getNickname())
					  .email(user.getEmail())
					  .profileImgUrl(user.getProfileImg() != null ? user.getProfileImg().getFileUrl() : null)
					  .title(user.getTitle() != null ? TitleDto.toDto(user.getTitle()) : null)
					  .address(user.getAddress() != null ? user.getAddress() : null)
					  .level(user.getLevel())
					  .exp(user.getExp())
					  .point(user.getPoint())
					  .build();
	}
}
