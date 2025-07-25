package com.explorer.gabom.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;

    public static LoginResponse toDto(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
