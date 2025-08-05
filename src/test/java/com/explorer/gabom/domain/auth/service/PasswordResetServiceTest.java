package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String CODE = "123456";
    private static final String WRONG_CODE = "654321";
    private static final String NEW_PASSWORD = "newPass!23";
    @InjectMocks
    private PasswordResetService passwordResetService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JavaMailSender emailSender;
    @Mock
    private EmailCodeStorageService emailCodeStorageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("인증코드 검증 성공")
    @Test
    void 인증코드_전송_성공() {
        // given
        PasswordResetRequest request = createResetRequest();

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
        doNothing().when(emailSender).send((MimeMessage) any());
        doNothing().when(emailCodeStorageService).savePasswordResetCode(any(), anyString(), anyLong());

        // when
        passwordResetService.sendResetCode(request);

        // then
        verify(userRepository).existsByEmail(EMAIL);
        verify(emailCodeStorageService).savePasswordResetCode(eq(request), anyString(), eq(300L));
        verify(emailSender).send(mailCaptor.capture());

        SimpleMailMessage mail = mailCaptor.getValue();
        assertEquals(EMAIL, mail.getTo()[0]);
        assertEquals("비밀번호 재설정 인증코드", mail.getSubject());
        assertTrue(mail.getText().contains("인증코드: "));
    }

    @DisplayName("인증코드 전송 가입안된 이메일 예외")
    @Test
    void 인증코드_전송_가입안된_이메일_예외() {
        PasswordResetRequest request = createResetRequest();

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class, () -> passwordResetService.sendResetCode(request));

        assertEquals(ErrorCode.EMAIL_NOT_FOUND, ex.getErrorCode());
        verify(emailSender, never()).send((MimeMessage) any());
        verify(emailCodeStorageService, never()).savePasswordResetCode(any(), anyString(), anyLong());
    }

    @DisplayName("비밀번호 재설정 성공")
    @Test
    void 비밀번호_재설정_성공() {
        PasswordResetVerifyRequest request = createVerifyRequest(CODE);

        User user = mock(User.class);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(CODE);
        when(userRepository.findByEmailAndStatus(EMAIL, UserStatus.ACTIVE)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("encodedPassword");
        doNothing().when(user).changePassword("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        passwordResetService.verifiedResetCode(request);

        verify(emailCodeStorageService).getPasswordResetCode(request);
        verify(userRepository).findByEmailAndStatus(EMAIL, UserStatus.ACTIVE);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(user).changePassword("encodedPassword");
        verify(userRepository).save(user);
    }

    @DisplayName("비밀번호 재설정 인증코드 없응 예외")
    @Test
    void 비밀번호_재설정_인증코드_없음_예외() {
        PasswordResetVerifyRequest request = createVerifyRequest(CODE);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () -> passwordResetService.verifiedResetCode(request));
        assertEquals(ErrorCode.EXPIRED_CODE, ex.getErrorCode());
    }

    @DisplayName("비밀번호 재설정 인증코드 불일치 예외")
    @Test
    void 비밀번호_재설정_인증코드_불일치_예외() {
        PasswordResetVerifyRequest request = createVerifyRequest(CODE);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(WRONG_CODE);

        CustomException ex = assertThrows(CustomException.class, () -> passwordResetService.verifiedResetCode(request));
        assertEquals(ErrorCode.CODE_NOT_MATCH, ex.getErrorCode());
    }

    @DisplayName("비밀번호 재설정 활성 사용자 없음 예외")
    @Test
    void 비밀번호_재설정_활성_사용자_없음_예외() {
        PasswordResetVerifyRequest request = createVerifyRequest(CODE);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(CODE);
        when(userRepository.findByEmailAndStatus(EMAIL, UserStatus.ACTIVE)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> passwordResetService.verifiedResetCode(request));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, ex.getErrorCode());
    }


    private PasswordResetRequest createResetRequest() {
        return new PasswordResetRequest(EMAIL);
    }

    private PasswordResetVerifyRequest createVerifyRequest(String code) {
        return new PasswordResetVerifyRequest(EMAIL, code, NEW_PASSWORD);
    }
}
