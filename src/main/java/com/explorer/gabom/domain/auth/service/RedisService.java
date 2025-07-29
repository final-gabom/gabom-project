package com.explorer.gabom.domain.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.auth.dto.request.EmailRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
	public final RedisTemplate<String, String> redisTemplate;

	// 인증 코드 저장
	public void saveEmailAuthCode(EmailRequest request, String authCode, long expirationSeconds) {
		String email = request.getEmail();
		redisTemplate.opsForValue().set("EMAIL_AUTH:" + email, authCode, expirationSeconds, TimeUnit.SECONDS);
	}

	// 인증 코드 조회
	public String getEmailAuthCode(EmailRequest request) {
		String email = request.getEmail();
		return redisTemplate.opsForValue().get("EMAIL_AUTH:" + email);
	}

	// 인증 코드 삭제
	public void deleteEmailAuthCode(EmailRequest request) {
		String email = request.getEmail();
		redisTemplate.delete("EMAIL_AUTH:" + email);
	}
}
