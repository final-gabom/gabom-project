package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailAuthServiceTest {

    @InjectMocks
    private EmailAuthService emailAuthService;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private EmailCodeStorageService emailCodeStorageService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === 인증 코드 전송 테스트 ===
    @Test
    void 이미_가입된_이메일_전송시_예외발생() {
        String email = "test@example.com";
        EmailRequest request = new EmailRequest(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> {
            emailAuthService.sendAuthCode(request);
        });

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());
        verify(emailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void 정상적으로_인증코드_전송_성공() {
        String email = "newuser@example.com";
        EmailRequest request = new EmailRequest(email);

        when(userRepository.existsByEmail(email)).thenReturn(false);

        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        emailAuthService.sendAuthCode(request);

        verify(emailCodeStorageService, times(1)).saveEmailAuthCode(
                eq(request),
                anyString(),
                eq(300L)
        );

        verify(emailSender).send(mailMessageCaptor.capture());

        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("이메일 인증 코드", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("인증 코드: "));
    }

    // === 인증 코드 검증 테스트 ===
    @Test
    void 인증코드_검증_이미_인증된_이메일() {
        String email = "test@example.com";
        String code = "123456";
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, code);

        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> {
            emailAuthService.verifyAuthCode(request);
        });

        assertEquals(ErrorCode.EMAIL_ALREADY_VERIFIED, ex.getErrorCode());
    }

    @Test
    void 인증코드_검증_만료된_코드() {
        String email = "test@example.com";
        String code = "123456";
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, code);

        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any(EmailRequest.class))).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () -> {
            emailAuthService.verifyAuthCode(request);
        });

        assertEquals(ErrorCode.EXPIRED_CODE, ex.getErrorCode());
    }

    @Test
    void 인증코드_검증_코드_불일치() {
        String email = "test@example.com";
        String inputCode = "123456";
        String savedCode = "654321";
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, inputCode);

        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any(EmailRequest.class))).thenReturn(savedCode);

        CustomException ex = assertThrows(CustomException.class, () -> {
            emailAuthService.verifyAuthCode(request);
        });

        assertEquals(ErrorCode.CODE_NOT_MATCH, ex.getErrorCode());
    }

    @Test
    void 인증코드_검증_성공() {
        String email = "test@example.com";
        String code = "123456";
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, code);

        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any(EmailRequest.class))).thenReturn(code);
        doNothing().when(emailCodeStorageService).deleteEmailAuthCode(any(EmailRequest.class));
        doNothing().when(emailCodeStorageService).setEmailVerified(request, 600);

        emailAuthService.verifyAuthCode(request);

        verify(emailCodeStorageService).deleteEmailAuthCode(any(EmailRequest.class));
        verify(emailCodeStorageService).setEmailVerified(request, 600);
    }
}
