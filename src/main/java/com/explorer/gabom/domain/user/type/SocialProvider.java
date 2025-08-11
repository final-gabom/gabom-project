package com.explorer.gabom.domain.user.type;

import com.explorer.gabom.global.oauth.type.OAuthProvider;

public enum SocialProvider {
	GOOGLE, KAKAO, NAVER;

	// OAuthProvider -> SocialProvider 변환 메서드
	public static SocialProvider fromOAuthProvider(OAuthProvider oauthProvider) {
		switch (oauthProvider) {
			case KAKAO: return KAKAO;
			case GOOGLE: return GOOGLE;
			case NAVER: return NAVER;
			default: throw new IllegalArgumentException("Unknown OAuthProvider: " + oauthProvider);
		}
	}
}
