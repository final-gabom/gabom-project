package com.explorer.gabom.domain.auth.oauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank
    private final String refreshtoken;
}
