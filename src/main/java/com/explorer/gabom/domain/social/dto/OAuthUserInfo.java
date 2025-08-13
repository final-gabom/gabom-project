package com.explorer.gabom.domain.social.dto;

import com.explorer.gabom.domain.social.type.SocialProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthUserInfo {
	private SocialProvider provider;
	private String providerId;
	private String email;
	private String Nickname;

	// 닉네임 없이 생성 가능하도록 오버로딩
	public OAuthUserInfo(SocialProvider provider, String providerId, String email) {
		this(provider, providerId, email, null);
	}
}
