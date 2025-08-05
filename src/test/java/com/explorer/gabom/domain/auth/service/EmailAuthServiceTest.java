package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailAuthServiceTest {

    // 공통 상수
    private static final String EMAIL = "test@example.com";
    private static final String CODE = "123456";
    private static final String WRONG_CODE = "654321";
    @InjectMocks
    private EmailAuthService emailAuthService;
    @Mock
    private JavaMailSender emailSender;
    @Mock
    private EmailCodeStorageService emailCodeStorageService;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @DisplayName("이미 가입된 이메일 전송시 예외발생")
    @Test
    void sendAuthCode_fail_emailAlreadyExists() {
        // given
        EmailRequest request = createEmailRequest();
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> emailAuthService.sendAuthCode(request));
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());

        verify(emailSender, never()).send(any(SimpleMailMessage.class));
    }
    @DisplayName("정성적으로 인증코드 전송 성공")
    @Test
    void sendAuthCode_success() {
        // given
        EmailRequest request = createEmailRequest();
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // when
        emailAuthService.sendAuthCode(request);

        // then
        verify(emailCodeStorageService).saveEmailAuthCode(eq(request), anyString(), eq(300L));
        verify(emailSender).send(mailCaptor.capture());

        SimpleMailMessage message = mailCaptor.getValue();
        assertEquals(EMAIL, message.getTo()[0]);
        assertEquals("이메일 인증 코드", message.getSubject());
        assertTrue(message.getText().contains("인증 코드: "));
    }
    @DisplayName("인증코드 검증 이미 인증된 이메일")
    @Test
    void verifyAuthCode_fail_emailAlreadyVerified() {
        EmailCodeVerifyRequest request = createVerifyRequest(CODE);
        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> emailAuthService.verifyAuthCode(request));
        assertEquals(ErrorCode.EMAIL_ALREADY_VERIFIED, ex.getErrorCode());
    }
    @DisplayName("인증코드 검증 만료된 코드")
    @Test
    void verifyAuthCode_fail_expiredCode() {
        EmailCodeVerifyRequest request = createVerifyRequest(CODE);
        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any())).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () -> emailAuthService.verifyAuthCode(request));
        assertEquals(ErrorCode.EXPIRED_CODE, ex.getErrorCode());
    }
    @DisplayName("인증코드 검증 코드 불일치")
    @Test
    void verifyAuthCode_fail_codeMismatch() {
        EmailCodeVerifyRequest request = createVerifyRequest(CODE);
        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any())).thenReturn(WRONG_CODE);

        CustomException ex = assertThrows(CustomException.class, () -> emailAuthService.verifyAuthCode(request));
        assertEquals(ErrorCode.CODE_NOT_MATCH, ex.getErrorCode());
    }
    @DisplayName("인증코드 검증 성공")
    @Test
    void verifyAuthCode_success() {
        EmailCodeVerifyRequest request = createVerifyRequest(CODE);
        when(emailCodeStorageService.isEmailVerified(request)).thenReturn(false);
        when(emailCodeStorageService.getEmailAuthCode(any())).thenReturn(CODE);
        doNothing().when(emailCodeStorageService).deleteEmailAuthCode(any());
        doNothing().when(emailCodeStorageService).setEmailVerified(eq(request), eq(600));

        emailAuthService.verifyAuthCode(request);

        verify(emailCodeStorageService).deleteEmailAuthCode(any());
        verify(emailCodeStorageService).setEmailVerified(request, 600);
    }

    // ========== 팩토리 메소드 ==========
    private EmailRequest createEmailRequest() {
        return new EmailRequest(EMAIL);
    }

    private EmailCodeVerifyRequest createVerifyRequest(String code) {
        return new EmailCodeVerifyRequest(EMAIL, code);
    }
}
