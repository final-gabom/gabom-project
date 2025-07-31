package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.global.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailCodeStorageService {
    private final RedisTemplate<String, String> redisTemplate;

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
    public boolean isEmailVerified(EmailCodeVerifyRequest verifiedRequest) {
        String email = verifiedRequest.getEmail();
        String checkEmail = redisTemplate.opsForValue().get("EMAIL_VERIFIED:" + email);
        return "true".equals(checkEmail);
    }

    // 인증 완료 상태 저장
    public void setEmailVerified(EmailCodeVerifyRequest verifiedRequest, long expirationSeconds) {
        String email = verifiedRequest.getEmail();
        redisTemplate.opsForValue().set("EMAIL_VERIFIED:" + email, "true", expirationSeconds, TimeUnit.SECONDS);
    }

    // 비밀번호 재설정코드 저장
    public void savePasswordResetCode(PasswordResetRequest request, String passWordCode,long expirationSeconds) {
        String email = request.getEmail();
        redisTemplate.opsForValue().set("PASSWORD_RESET_CODE:" + email, passWordCode, expirationSeconds, TimeUnit.SECONDS);
    }
    // 저장된 비밀번호 재설정 인증코드 조회
    public String getPasswordResetCode(PasswordResetVerifyRequest request) {
        String email = request.getEmail();
        return redisTemplate.opsForValue().get("PASSWORD_RESET_CODE:" + email);
    }
    // 비밀번호 재설정 코드 인증 완료 여부 확인
    public boolean isPasswordResetVerified(PasswordResetVerifyRequest verifyRequest) {
        String email = verifyRequest.getEmail();
        String checkResetEmail = redisTemplate.opsForValue().get("PASSWORD_RESET_VERIFIED:" + email);
        return "true".equals(checkResetEmail);
    }
    // 비밀번호 재설정 인증 완료 상태 저장
    public void setPasswordResetVerified(PasswordResetVerifyRequest request, long expirationSeconds) {
        String email = request.getEmail();
        redisTemplate.opsForValue().set("PASSWORD_RESET_VERIFIED:" + email, "true", expirationSeconds, TimeUnit.SECONDS);
    }
    // 비밀번호 재설정 인증 코드 삭제
    public void deletePasswordResetCode(PasswordResetVerifyRequest request) {
        String email = request.getEmail();
        redisTemplate.delete("PASSWORD_RESET_CODE:" + email);
    }
}
