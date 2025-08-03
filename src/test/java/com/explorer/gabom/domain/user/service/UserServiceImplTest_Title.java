package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserServiceImpl_TitleTest {

    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private com.explorer.gabom.domain.file.repository.AttachmentFileRepository fileRepository;

    @Mock
    private com.explorer.gabom.global.validator.PasswordValidator passwordValidator;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, fileRepository, titleRepository, passwordValidator, passwordEncoder);

        user = User.builder()
                .email("test@example.com")
                .nickname("testUser")
                .userRole(UserRole.USER)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "status", UserStatus.ACTIVE);  // 여기 있어야 안전함
    }

    @Test
    void updateMainTitle_성공() {
        // 준비
        Long titleId = 1L;
        Title title = new Title("야호","열정의 모험가");
        ReflectionTestUtils.setField(title, "id", titleId);

        ReflectionTestUtils.setField(user, "status", UserStatus.ACTIVE);

        when(titleRepository.findById(titleId)).thenReturn(Optional.of(title));

        // 실행
        UpdateMainTitleResponse response = userService.updateMainTitle(user, titleId);

        // 검증
        assertThat(response.getTitleId()).isEqualTo(titleId);
        assertThat(response.getTitleName()).isEqualTo("야호");
    }


    @Test
    @DisplayName("존재하지 않는 칭호 ID")
    void updateMainTitle_실패_존재하지않는칭호() {
        // given
        Long invalidTitleId = 999L;
        when(titleRepository.findById(invalidTitleId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateMainTitle(user, invalidTitleId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TITLE_NOT_FOUND);
    }

    @Test
    @DisplayName("비활성화된 유저는 칭호 변경 불가")
    void updateMainTitle_실패_비활성유저() {
        // given
        Long titleId = 1L;
        Title title = new Title("야호", "열정의 모험가");
        ReflectionTestUtils.setField(user, "status", UserStatus.INACTIVE);
        when(titleRepository.findById(titleId)).thenReturn(Optional.of(title));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateMainTitle(user, titleId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}

