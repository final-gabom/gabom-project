package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String OLD_NICKNAME = "testUser";

    private static final String NEW_NICKNAME = "newNick";
    private static final String NEW_ADDRESS = "서울시 강남구";
    private static final Double NEW_LAT = 37.5;
    private static final Double NEW_LNG = 127.0;
    private static final String PROFILE_IMG_ID = "file-123";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttachmentFileRepository fileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private TitleRepository titleRepository;

    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, fileRepository, titleRepository, passwordValidator, passwordEncoder);

        user = createUser(USER_ID, EMAIL, OLD_NICKNAME);
    }

    private User createUser(Long id, String email, String nickname) {
        return User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .build();
    }

    private UserUpdateRequest createUpdateRequest(String nickname, String address, Double lat, Double lng, String profileImgId) {
        return new UserUpdateRequest(nickname, address, lat, lng, profileImgId);
    }

    private AttachmentFile createAttachmentFile(String fileId) {
        AttachmentFile file = AttachmentFile.builder()
                .fileType(FileType.PROFILE)
                .fileName("img.png")
                .fileSize(12345L)
                .refId(USER_ID)
                .mimeType("image/png")
                .hash("hash")
                .filePath("/path/to/img.png")
                .orderIdx(0)
                .build();

        ReflectionTestUtils.setField(file, "fileId", fileId);
        return file;
    }

    @Nested
    class getUser {

        @Test
        @DisplayName("성공")
        void getUser_성공() {
            when(userRepository.findByIdAndStatus(USER_ID, UserStatus.ACTIVE)).thenReturn(Optional.of(user));

            UserDto userDto = userService.getUser(USER_ID);

            assertThat(userDto).isNotNull();
            assertThat(userDto.getId()).isEqualTo(USER_ID);
            assertThat(userDto.getNickname()).isEqualTo(OLD_NICKNAME);
            assertThat(userDto.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("실패_UserNotFound")
        void getUser_실패() {
            when(userRepository.findByIdAndStatus(USER_ID, UserStatus.ACTIVE)).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> userService.getUser(USER_ID));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 사용자정보_수정_성공() {
            UserUpdateRequest updateRequest = createUpdateRequest(NEW_NICKNAME, NEW_ADDRESS, NEW_LAT, NEW_LNG, PROFILE_IMG_ID);
            AttachmentFile mockFile = createAttachmentFile(PROFILE_IMG_ID);

            when(userRepository.existsByNickname(NEW_NICKNAME)).thenReturn(false);
            when(fileRepository.findById(PROFILE_IMG_ID)).thenReturn(Optional.of(mockFile));

            UserDto updatedUser = userService.updateUser(user, updateRequest);

            assertThat(updatedUser.getNickname()).isEqualTo(NEW_NICKNAME);
            assertThat(updatedUser.getAddress()).isEqualTo(NEW_ADDRESS);
            assertThat(user.getLat()).isEqualTo(NEW_LAT);
            assertThat(user.getLng()).isEqualTo(NEW_LNG);
            assertThat(user.getProfileImg()).isEqualTo(mockFile);
        }

        @Test
        void 닉네임중복시_예외발생() {
            String duplicatedNick = "existingNick";
            UserUpdateRequest updateRequest = createUpdateRequest(duplicatedNick, "주소", 1.0, 2.0, null);

            when(userRepository.existsByNickname(duplicatedNick)).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser(user, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        @Test
        void 존재하지않는파일일때_예외발생() {
            String profileImgId = "not-found-id";
            UserUpdateRequest updateRequest = createUpdateRequest(NEW_NICKNAME, "주소", 1.0, 2.0, profileImgId);

            when(userRepository.existsByNickname(NEW_NICKNAME)).thenReturn(false);
            when(fileRepository.findById(profileImgId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(user, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.FILE_NOT_FOUND.getMessage());
        }
    }
}
