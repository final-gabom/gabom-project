package com.explorer.gabom.domain.auth.service;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {
	private final RedisService redisService;
	private final JavaMailSender mailSender;

	private static final long EMAIL_AUTH_CODE_EXPIRATION = 300L; // 5분

	// 인증 코드 생성 및 이메일 발송
	public void sendAuthCode(String toEmail) {
		String authCode = generateAuthCode();
		log.debug("발송 대상: {}, 인증번호: {}", toEmail, authCode);

		// Redis에 인증 코드 저장
		redisService.saveEmailAuthCode(toEmail, authCode, EMAIL_AUTH_CODE_EXPIRATION);

		// 이메일 전송
		sendEmail(toEmail, authCode);
	}

	// 이메일 전송 로직
	private void sendEmail(String toEmail, String authCode) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("[Gabom] 이메일 인증번호 안내");
		message.setText("인증번호는 다음과 같습니다: " + authCode);
		mailSender.send(message);
	}

	// 6자리 인증 코드 생성
	private String generateAuthCode() {
		Random random = new Random();
		return String.format("%06d", random.nextInt(1000000)); // 6자리 숫자
	}
}
