package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final EmailCodeStorageService emailCodeStorageService;

    private static final long PASSWORD_RESET_CODE_EXPIRATION_SECONDS = 300;
    private final PasswordEncoder passwordEncoder;

    // 인증코드 전송
    @Transactional
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

    // 비밀번호 재설정을 위한 이메일 검증 + 비밀번호 재설정
    @Transactional
    public void verifiedResetCode(PasswordResetVerifyRequest request){
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getNewPassword();

        log.info("[비밀번호 재설정 시작] 이메일: {}", email);


        // redis에 저장된 비밀번호 재설정 인증 코드 조회
        String savedCode = emailCodeStorageService.getPasswordResetCode(request);
        if (savedCode == null) {
            log.warn("[비밀번호 재설정 실패] 인증 코드 만료 또는 없음: {}", email);
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }

        // 코드 일치 여부 확인
        if (!savedCode.equals(code)) {
            log.warn("[비밀번호 재설정 실패] 인증 코드 불일치: 이메일={}, 입력코드={}, 저장코드={}", email, code, savedCode);
            throw new CustomException(ErrorCode.CODE_NOT_MATCH);
        }

        // 사용자 조회
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("[비밀번호 재설정 실패] 활성 사용자 없음: {}", email);
                return new CustomException(ErrorCode.EMAIL_NOT_FOUND);
                });
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);

        // 사용자 저장
        userRepository.save(user);

        log.info("[비밀번호 재설정 완료] 이메일: {}", email);
    }
}
