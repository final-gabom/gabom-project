package com.explorer.gabom.domain.user.dto;

import com.explorer.gabom.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    @Schema(description = "유저 ID", example = "1")
    private Long id;

    @Schema(description = "이메일 주소", example = "gabom@example.com")
    private String email;

    @Schema(description = "유저 닉네임", example = "gabomUser")
    private String nickname;

    @Schema(description = "유저 레벨", example = "10")
    private Integer level;

    @Schema(description = "대표 칭호 이름", example = "열정의 모험가", nullable = true)
    private String title;

    public static UserSummaryDto toDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .level(user.getLevel())
                .title(user.getTitle() != null ? user.getTitle().getName() : null)
                .build();
    }
}
