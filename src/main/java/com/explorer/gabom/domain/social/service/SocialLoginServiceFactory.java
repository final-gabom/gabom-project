package com.explorer.gabom.domain.social.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.social.type.SocialProvider;

@Component
public class SocialLoginServiceFactory {

	private final Map<String, SocialLoginService> serviceMap;

	public SocialLoginServiceFactory(List<SocialLoginService> serviceList) {
		this.serviceMap = new HashMap<>();
		for (SocialLoginService service : serviceList) {
			serviceMap.put(service.getProvider().name(), service);
		}
	}

	public SocialLoginService getService(SocialProvider provider) {
		String key = provider.name();
		if (!serviceMap.containsKey(key)) {
			throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
		}
		return serviceMap.get(key);
	}
}