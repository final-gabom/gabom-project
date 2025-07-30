package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.auth.dto.request.EmailRequest;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

	private final JavaMailSender emailSender;
	private final RedisService redisService;

	private static final long AUTH_CODE_EXPIRATION_SECONDS = 300;
	private final UserRepository userRepository;

	public void sendAuthCode(EmailRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		String authCode = generateRandomCode();
		redisService.saveEmailAuthCode(request, authCode, AUTH_CODE_EXPIRATION_SECONDS);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(request.getEmail());
		message.setSubject("이메일 인증 코드");
		message.setText("인증 코드: " + authCode);

		emailSender.send(message);
	}

	private String generateRandomCode() {
		return String.format("%06d", (int) (Math.random() * 1000000));
	}
}
