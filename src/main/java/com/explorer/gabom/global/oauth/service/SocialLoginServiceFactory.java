package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.global.oauth.type.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SocialLoginServiceFactory {

    private final Map<String, SocialOAuthLoginService> serviceMap;

    public SocialLoginServiceFactory(List<SocialOAuthLoginService> serviceList) {
        this.serviceMap = new HashMap<>();
        for (SocialOAuthLoginService service : serviceList) {
            serviceMap.put(service.getProvider().name(), service);
        }
    }

    public SocialOAuthLoginService getService(OAuthProvider provider) {
        String key = provider.name();
        if (!serviceMap.containsKey(key)) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
        return serviceMap.get(key);
    }
}


