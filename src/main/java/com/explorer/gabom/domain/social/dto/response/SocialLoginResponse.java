package com.explorer.gabom.domain.social.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SocialLoginResponse {

	private final boolean signedUp;
	private final Long tempId;
	private final String accessToken;
	private final String refreshToken;

	public static SocialLoginResponse loginSuccess(String accessToken, String refreshToken) {
		return SocialLoginResponse.builder()
								  .signedUp(true)
								  .accessToken(accessToken)
								  .refreshToken(refreshToken)
								  .build();
	}

	public static SocialLoginResponse signupRequired(Long tempId) {
		return SocialLoginResponse.builder()
								  .signedUp(false)
								  .tempId(tempId)
								  .build();
	}
}
