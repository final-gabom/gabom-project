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

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "test@example.com";
    private static final String USER_NICKNAME = "testUser";

    private static final Long TITLE_ID = 1L;
    private static final String TITLE_NAME = "야호";
    private static final String TITLE_DESCRIPTION = "열정의 모험가";

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

        user = createActiveUser(USER_ID, USER_EMAIL, USER_NICKNAME);
    }

    private User createActiveUser(Long id, String email, String nickname) {
        User u = User.builder()
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .build();

        ReflectionTestUtils.setField(u, "id", id);
        ReflectionTestUtils.setField(u, "status", UserStatus.ACTIVE);

        return u;
    }

    private Title createTitle(Long id, String name, String description) {
        Title t = new Title(name, description);
        ReflectionTestUtils.setField(t, "id", id);
        return t;
    }

    @DisplayName("칭호변경 성공")
    @Test
    void updateMainTitle_Success() {
        Title title = createTitle(TITLE_ID, TITLE_NAME, TITLE_DESCRIPTION);

        when(titleRepository.findById(TITLE_ID)).thenReturn(Optional.of(title));

        UpdateMainTitleResponse response = userService.updateMainTitle(user, TITLE_ID);

        assertThat(response.getTitleId()).isEqualTo(TITLE_ID);
        assertThat(response.getTitleName()).isEqualTo(TITLE_NAME);
    }

    @Test
    @DisplayName("존재하지 않는 칭호 ID")
    void titleNotFound_throwsException() {
        Long invalidTitleId = 999L;
        when(titleRepository.findById(invalidTitleId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateMainTitle(user, invalidTitleId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TITLE_NOT_FOUND);
    }

    @Test
    @DisplayName("비활성화된 유저는 칭호 변경 불가")
    void inactiveUser_cannotChangeTitle() {
        Title title = createTitle(TITLE_ID, TITLE_NAME, TITLE_DESCRIPTION);
        ReflectionTestUtils.setField(user, "status", UserStatus.INACTIVE);

        when(titleRepository.findById(TITLE_ID)).thenReturn(Optional.of(title));

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateMainTitle(user, TITLE_ID));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}


