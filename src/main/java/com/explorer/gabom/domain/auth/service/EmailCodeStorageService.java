package com.explorer.gabom.domain.auth.service;

import java.util.concurrent.TimeUnit;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.auth.dto.request.EmailRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailCodeStorageService {
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

	// 인증 완료 여부 확인
	public boolean isEmailVerified(EmailCodeVerifyRequest request) {
		String email = request.getEmail();
		String checkEmail = redisTemplate.opsForValue().get("EMAIL_VERIFIED:"+ email);
		return "true".equals(checkEmail);
	}
	// 인증 완료 상태 저장
	public void setEmailVerified(EmailCodeVerifyRequest request, long expirationSeconds) {
		String email = request.getEmail();
		redisTemplate.opsForValue().set("EMAIL_VERIFIED:" + email,"true",expirationSeconds, TimeUnit.SECONDS);
	}
}
