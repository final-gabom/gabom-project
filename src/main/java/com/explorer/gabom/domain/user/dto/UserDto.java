package com.explorer.gabom.domain.user.dto;

import com.explorer.gabom.domain.title.dto.TitleDto;
import com.explorer.gabom.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {
	@Schema(description = "유저 고유 ID", example = "1")
	private final Long id;

	@Schema(description = "닉네임", example = "gabom")
	private final String nickname;

	@Schema(description = "이메일 주소", example = "gabom@example.com")
	private final String email;

	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
	private final String profileImgUrl;

	@Schema(description = "칭호 정보", nullable = true)
	private final TitleDto title;

	@Schema(description = "주소", example = "서울시 강남구", nullable = true)
	private final String address;

	@Schema(description = "레벨", example = "5")
	private final Integer level;

	@Schema(description = "경험치", example = "1234")
	private final Integer exp;

	@Schema(description = "포인트", example = "5000")
	private final Integer point;

	public static UserDto toDto(User user) {
		return UserDto.builder().id(user.getId())
					  .nickname(user.getNickname())
					  .email(user.getEmail())
					  .profileImgUrl(user.getProfileImg() != null ? user.getProfileImg().getFilePath() : null)
					  .title(user.getTitle() != null ? TitleDto.toDto(user.getTitle()) : null)
					  .level(user.getLevel())
					  .exp(user.getExp())
					  .point(user.getPoint())
					  .build();
	}
}
