package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
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
public class EmailAuthService {

    private static final long AUTH_CODE_EXPIRATION_SECONDS = 300; //5분
    private final JavaMailSender emailSender;
    private final EmailCodeStorageService emailCodeStorageService;
    private final UserRepository userRepository;

    // 인증 코드 전송
    public void sendAuthCode(EmailRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("이미 가입된 이메일 요청: {}", email);
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        String authCode = generateRandomCode();
        emailCodeStorageService.saveEmailAuthCode(request, authCode, AUTH_CODE_EXPIRATION_SECONDS);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + authCode);

        emailSender.send(message);
        log.info("이메일 인증 코드 전송 완료: {} => {}", email, authCode);
    }

    // 랜덤 코드 생성
    private String generateRandomCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    // 이메일 인증코드 검증
    public void verifyAuthCode(EmailCodeVerifyRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        log.debug("이메일 인증 요청: email={}, code={}", email, code);

        //이미 인증된 이메일인지 확인
        if (emailCodeStorageService.isEmailVerified(request)) {
            log.warn("이미 인증된 이메일: {}", email);
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // redis에 저장된 인증코드 가져오기
        String savedCode = emailCodeStorageService.getEmailAuthCode(new EmailRequest(email));
        if (savedCode == null) {
            log.warn("인증 코드 만료 또는 없음: {}", email);
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }
        // 코드 불일치
        if (!savedCode.equals(code)) {
            log.warn("인증 코드 불일치: 입력={}, 저장={}", code, savedCode);
            throw new CustomException(ErrorCode.CODE_NOT_MATCH);
        }
        // 인증된 코드삭제
        emailCodeStorageService.deleteEmailAuthCode(new EmailRequest(email));

        // 인증 상태를 유지(10분 유지)
        emailCodeStorageService.setEmailVerified(request, 600);
        log.info("이메일 인증 성공: {}", email);
    }
}
