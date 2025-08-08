package com.explorer.gabom.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.address.service.AddressService;
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

class UserServiceImplTest {

	private static final Long USER_ID = 1L;
	private static final String EMAIL = "test@example.com";
	private static final String OLD_NICKNAME = "testUser";
	private static final String NEW_NICKNAME = "newNick";
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

	@Mock
	private AddressService addressService;

	private UserServiceImpl userService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepository, fileRepository, titleRepository, passwordValidator,
										  passwordEncoder, addressService);

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

	private UserUpdateRequest createUpdateRequest(String nickname, String profileImgId) {
		return new UserUpdateRequest(nickname, profileImgId);
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
	}

	@Nested
	@DisplayName("updateUser")
	class Describe_updateUser {
		@Test
		@DisplayName("성공: 닉네임·프로필 이미지 모두 업데이트")
		void it_updates_nickname_address_and_profileImage() {
			// given
			UserUpdateRequest req = createUpdateRequest(
				NEW_NICKNAME, PROFILE_IMG_ID
			);
			AttachmentFile mockFile = createAttachmentFile(PROFILE_IMG_ID);

			given(userRepository.existsByNickname(NEW_NICKNAME)).willReturn(false);
			given(fileRepository.findById(PROFILE_IMG_ID)).willReturn(Optional.of(mockFile));

			// when
			UserDto updated = userService.updateUser(user, req);

			// then
			assertThat(updated.getNickname()).isEqualTo(NEW_NICKNAME);
			assertThat(updated.getProfileImgUrl()).isEqualTo(mockFile.getFilePath());
		}

		@Test
		@DisplayName("실패: 닉네임 중복 시 NICKNAME_ALREADY_EXISTS 예외")
		void it_throws_when_duplicate_nickname() {
			String dupNick = "existNick";
			UserUpdateRequest req = createUpdateRequest(
				dupNick, null
			);
			given(userRepository.existsByNickname(dupNick)).willReturn(true);

			assertThatThrownBy(() -> userService.updateUser(user, req))
				.isInstanceOf(CustomException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		@Test
		@DisplayName("실패: 프로필 이미지 ID 없으면 FILE_NOT_FOUND 예외")
		void it_throws_when_file_not_found() {
			// given
			String badFileId = "no-such-file";
			UserUpdateRequest req = createUpdateRequest(
				NEW_NICKNAME, badFileId
			);

			given(userRepository.existsByNickname(NEW_NICKNAME)).willReturn(false);

			given(fileRepository.findById(badFileId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> userService.updateUser(user, req))
				.isInstanceOf(CustomException.class)
				.extracting("errorCode")
				.isEqualTo(ErrorCode.FILE_NOT_FOUND);
		}
	}
}
