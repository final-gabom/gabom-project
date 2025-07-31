package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final EmailCodeStorageService emailCodeStorageService;

    private static final long PASSWORD_RESET_CODE_EXPIRATION_SECONDS = 300;

    // 인증코드 전송
    public void sendResetCode(PasswordResetRequest request) {
        String email = request.getEmail();
        if (!userRepository.existsByEmail(email)) {
            log.warn("가입되지 않은 이메일 요청: {}", email);
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }
        String authCode = generateRandomCode();
        emailCodeStorageService.savePasswordResetCode(request, authCode, PASSWORD_RESET_CODE_EXPIRATION_SECONDS);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 재설정 인증코드");
        message.setText("인증코드: " + authCode);

        emailSender.send(message);
        log.info("비밀번호 재설정 인증 코드 전송 완료: {} => {}", email, authCode);
    }
    // 랜덤 코드 생성
    private String generateRandomCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
