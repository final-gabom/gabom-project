package com.explorer.gabom.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.file.repository.AttachmentFileRepository;

class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private AttachmentFileRepository attachmentFileRepository;
	private UserServiceImpl userService;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepository, attachmentFileRepository);

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
	}

}