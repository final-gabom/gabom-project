package com.explorer.gabom.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
    }


    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
