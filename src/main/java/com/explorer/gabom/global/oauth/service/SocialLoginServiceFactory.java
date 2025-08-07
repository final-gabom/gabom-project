package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.global.oauth.type.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
@Component
public class SocialLoginServiceFactory {
    private final Map<OAuthProvider, SocialOAuthLoginService> serviceMap = new EnumMap<>(OAuthProvider.class);

    public SocialLoginServiceFactory(List<SocialOAuthLoginService> serviceList) {
        for (SocialOAuthLoginService service : serviceList) {
            serviceMap.put(service.getProvider(), service);
        }
    }

    public SocialOAuthLoginService getService(OAuthProvider provider) {
        if (!serviceMap.containsKey(provider)) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
        return serviceMap.get(provider);
    }
}

