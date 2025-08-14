package com.explorer.gabom.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "로그인 시 사용될 액세스 토큰")
    private final String accessToken;
    @Schema(description = "로그인 시 사용될 리프레쉬 토큰")
    private final String refreshToken;

    private final boolean isNewUser;

    public static LoginResponse toDto(String accessToken, String refreshToken, boolean isNewUser) {
        return new LoginResponse(accessToken, refreshToken, isNewUser);
    }
}
