package com.explorer.gabom.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.logging.Logger;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.global.validator.PasswordValidator;
import org.springframework.test.util.ReflectionTestUtils;

class UserServiceImplTest {

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
	private UserServiceImpl userService;

	@Mock
	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepository, fileRepository,titleRepository, passwordValidator, passwordEncoder);

		// 공통 User 객체 생성
		Long userId = 1L;
		user = User.builder()
				   .id(userId)
				   .email("test@example.com")
				   .password("password123$")
				   .nickname("testUser")
				   .userRole(UserRole.USER)
				   .build();
	}


	@Nested
	class getUser {
		@Test
		@DisplayName("성공")
		void getUser_성공() {
			// given
			Long userId = 1L;
			when(userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)).thenReturn(Optional.of(user));

			// when
			UserDto userDto = userService.getUser(userId);

			// then
			assertThat(userDto).isNotNull();
			assertThat(userDto.getId()).isEqualTo(userId);
			assertThat(userDto.getNickname()).isEqualTo("testUser");
			assertThat(userDto.getEmail()).isEqualTo("test@example.com");
		}

		@Test
		@DisplayName("실패_UserNotFound")
		void getUser_실패() {
			// given
			Long userId = 1L;
			when(userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)).thenReturn(Optional.empty());

			// when & then
			CustomException exception = assertThrows(CustomException.class, () -> userService.getUser(userId));
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
		}

		@Test
		void 사용자정보_수정_성공() {
			// given
			String newNickname = "newNick";
			String newAddress = "서울시 강남구";
			Double newLat = 37.5;
			Double newLng = 127.0;
			String profileImgId = "file-123";

			UserUpdateRequest updateRequest = new UserUpdateRequest(
					newNickname, newAddress, newLat, newLng, profileImgId
			);

			// 빌더로 만든 후 fileId 직접 세팅
			AttachmentFile mockFile = AttachmentFile.builder()
					.fileType(FileType.PROFILE)
					.fileName("img.png")
					.fileSize(12345L)
					.refId(1L)
					.mimeType("image/png")
					.hash("hash")
					.filePath("/path/to/img.png")
					.orderIdx(0)
					.build();

			// fileId 직접 주입
			ReflectionTestUtils.setField(mockFile, "fileId", profileImgId);

			when(userRepository.existsByNickname(newNickname)).thenReturn(false);
			when(fileRepository.findById(profileImgId)).thenReturn(Optional.of(mockFile));

			// when
			UserDto updatedUser = userService.updateUser(user, updateRequest);

			// then
			assertThat(updatedUser.getNickname()).isEqualTo(newNickname);
			assertThat(updatedUser.getAddress()).isEqualTo(newAddress);
			assertThat(user.getLat()).isEqualTo(newLat);
			assertThat(user.getLng()).isEqualTo(newLng);
			assertThat(user.getProfileImg()).isEqualTo(mockFile);
		}


		@Test
		void 닉네임중복시_예외발생() {
			// given
			String duplicatedNick = "existingNick";
			UserUpdateRequest updateRequest = new UserUpdateRequest(
					duplicatedNick, "주소", 1.0, 2.0, null
			);

			when(userRepository.existsByNickname(duplicatedNick)).thenReturn(true);

			// expect
			assertThatThrownBy(() -> userService.updateUser(user, updateRequest))
					.isInstanceOf(CustomException.class)
					.hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
		}

		@Test
		void 존재하지않는파일일때_예외발생() {
			// given
			String newNick = "newNick";
			String profileImgId = "not-found-id";
			UserUpdateRequest updateRequest = new UserUpdateRequest(
					newNick, "주소", 1.0, 2.0, profileImgId
			);

			when(userRepository.existsByNickname(newNick)).thenReturn(false);
			when(fileRepository.findById(profileImgId)).thenReturn(Optional.empty());

			// expect
			assertThatThrownBy(() -> userService.updateUser(user, updateRequest))
					.isInstanceOf(CustomException.class)
					.hasMessageContaining(ErrorCode.FILE_NOT_FOUND.getMessage());
		}
	}

}