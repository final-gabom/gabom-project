package com.explorer.gabom.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokens {
	private final String accessToken;
	private final String refreshToken;
}
