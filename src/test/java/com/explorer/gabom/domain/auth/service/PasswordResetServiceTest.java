package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

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
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 인증코드 전송: 가입된 이메일일 때 정상 호출 테스트
    @Test
    void 인증코드_전송_성공() {
        String email = "user@example.com";
        PasswordResetRequest request = new PasswordResetRequest(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));
        doNothing().when(emailCodeStorageService).savePasswordResetCode(any(), anyString(), anyLong());

        passwordResetService.sendResetCode(request);

        verify(userRepository, times(1)).existsByEmail(email);
        verify(emailCodeStorageService, times(1)).savePasswordResetCode(eq(request), anyString(), eq(300L));
        verify(emailSender, times(1)).send(mailMessageCaptor.capture());

        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("비밀번호 재설정 인증코드", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("인증코드: "));
    }

    // 2. 인증코드 전송: 가입 안 된 이메일일 때 예외 발생 테스트
    @Test
    void 인증코드_전송_가입안된_이메일_예외() {
        String email = "unknown@example.com";
        PasswordResetRequest request = new PasswordResetRequest(email);

        when(userRepository.existsByEmail(email)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class, () -> {
            passwordResetService.sendResetCode(request);
        });

        assertEquals(ErrorCode.EMAIL_NOT_FOUND, ex.getErrorCode());
        verify(emailSender, never()).send(any(SimpleMailMessage.class));
        verify(emailCodeStorageService, never()).savePasswordResetCode(any(), anyString(), anyLong());
    }

    // 3. 비밀번호 재설정 검증 및 변경 성공 테스트
    @Test
    void 비밀번호_재설정_성공() {
        String email = "user@example.com";
        String code = "123456";
        String newPassword = "newPass!23";

        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest(email, code, newPassword);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(code);

        User user = mock(User.class);
        when(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // user.changePassword() 는 void 메서드이므로 mock 상태에서 doNothing()
        doNothing().when(user).changePassword("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        passwordResetService.verifiedResetCode(request);

        verify(emailCodeStorageService, times(1)).getPasswordResetCode(request);
        verify(userRepository, times(1)).findByEmailAndStatus(email, UserStatus.ACTIVE);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(user, times(1)).changePassword("encodedPassword");
        verify(userRepository, times(1)).save(user);
    }

    // 4. 비밀번호 재설정 실패 - 인증코드 만료 또는 없음
    @Test
    void 비밀번호_재설정_인증코드_없음_예외() {
        String email = "user@example.com";
        String code = "123456";
        String newPassword = "newPass!23";

        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest(email, code, newPassword);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class, () -> {
            passwordResetService.verifiedResetCode(request);
        });

        assertEquals(ErrorCode.EXPIRED_CODE, ex.getErrorCode());
    }

    // 5. 비밀번호 재설정 실패 - 인증코드 불일치
    @Test
    void 비밀번호_재설정_인증코드_불일치_예외() {
        String email = "user@example.com";
        String inputCode = "123456";
        String savedCode = "654321";
        String newPassword = "newPass!23";

        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest(email, inputCode, newPassword);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(savedCode);

        CustomException ex = assertThrows(CustomException.class, () -> {
            passwordResetService.verifiedResetCode(request);
        });

        assertEquals(ErrorCode.CODE_NOT_MATCH, ex.getErrorCode());
    }

    // 6. 비밀번호 재설정 실패 - 활성 사용자 없음
    @Test
    void 비밀번호_재설정_활성_사용자_없음_예외() {
        String email = "user@example.com";
        String code = "123456";
        String newPassword = "newPass!23";

        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest(email, code, newPassword);

        when(emailCodeStorageService.getPasswordResetCode(request)).thenReturn(code);
        when(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> {
            passwordResetService.verifiedResetCode(request);
        });

        assertEquals(ErrorCode.EMAIL_NOT_FOUND, ex.getErrorCode());
    }
}
