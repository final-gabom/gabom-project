package com.explorer.gabom.domain.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
	public final RedisTemplate<String,String> redisTemplate;


	// 인증 코드 저장
	public void saveEmailAuthCode(String email, String authCode, long expirationSeconds) {
		redisTemplate.opsForValue().set("EMAIL_AUTH:" + email, authCode, expirationSeconds, TimeUnit.SECONDS);
	}
	// 인증 코드 조회
	public String getEmailAuthCode(String email) {
		return redisTemplate.opsForValue().get("EMAIL_AUTH:" + email);
	}
	// 인증 코드 삭제
	public void deleteEmailAuthCode(String email) {
		redisTemplate.delete("EMAIL_AUTH:" + email);
	}
}
