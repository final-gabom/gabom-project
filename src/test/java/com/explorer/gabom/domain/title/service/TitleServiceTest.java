package com.explorer.gabom.domain.title.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class TitleServiceTest {

	@InjectMocks
	private TitleService titleService;

	@Mock
	private TitleRepository titleRepository;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("createTitle - 성공")
	void createTitle_success() {
		// Given
		TitleCreateRequest request = new TitleCreateRequest("모험가", "첫 퀘스트 완료 시 지급");
		Title saved = new Title("모험가", "첫 퀘스트 완료 시 지급");
		ReflectionTestUtils.setField(saved, "id", 1L); // id 수동 설정

		given(titleRepository.existsByName("모험가")).willReturn(false);
		given(titleRepository.save(any(Title.class))).willReturn(saved);

		// When
		TitleCreateResponse response = titleService.createTitle(request);

		// Then
		assertEquals(1L, response.getId());
		assertEquals("모험가", response.getName());
	}

	@Test
	@DisplayName("createTitle - 실패 (이미 존재하는 이름)")
	void createTitle_fail_alreadyExists() {
		// Given
		TitleCreateRequest request = new TitleCreateRequest("모험가", "중복");

		given(titleRepository.existsByName("모험가")).willReturn(true);

		// When & Then
		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.createTitle(request));
		assertEquals(ErrorCode.TITLE_ALREADY_EXISTS, e.getErrorCode());
	}

	@Test
	@DisplayName("updateTitle - 성공")
	void updateTitle_success() {
		// Given
		TitleUpdateRequest request = new TitleUpdateRequest("수정된 이름", "수정된 설명");
		Title existing = new Title("기존", "기존 설명");
		ReflectionTestUtils.setField(existing, "id", 1L);

		given(titleRepository.findById(1L)).willReturn(Optional.of(existing));

		// When
		titleService.updateTitle(1L, request);

		// Then
		verify(titleRepository).updateTitle(1L, "수정된 이름", "수정된 설명");
	}

	@Test
	@DisplayName("updateTitle - 실패 (존재하지 않음)")
	void updateTitle_fail_notFound() {
		// Given
		TitleUpdateRequest request = new TitleUpdateRequest("수정", "수정 설명");
		given(titleRepository.findById(99L)).willReturn(Optional.empty());

		// When & Then
		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.updateTitle(99L, request));
		assertEquals(ErrorCode.TITLE_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("deleteTitle - 성공")
	void deleteTitle_success() {
		// Given
		Title title = new Title("삭제할 제목", "설명");
		ReflectionTestUtils.setField(title, "id", 1L);

		given(titleRepository.findById(1L)).willReturn(Optional.of(title));

		// When
		TitleDeleteResponse response = titleService.deleteTitle(1L);

		// Then
		verify(titleRepository).delete(title);
		assertEquals(1L, response.getId());
	}

	@Test
	@DisplayName("deleteTitle - 실패 (존재하지 않음)")
	void deleteTitle_fail_notFound() {
		// Given
		given(titleRepository.findById(404L)).willReturn(Optional.empty());

		// When & Then
		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.deleteTitle(404L));
		assertEquals(ErrorCode.TITLE_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("getUserTitles - 성공")
	void getUserTitles_success() {
		// Given
		User user = new User();
		ReflectionTestUtils.setField(user, "id", 1L);
		UserTitle userTitle = new UserTitle(); // 생성자 필요에 따라 수정
		ReflectionTestUtils.setField(userTitle, "id", 10L);
		ReflectionTestUtils.setField(user, "userTitles", List.of(userTitle));

		given(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).willReturn(Optional.of(user));

		// When
		List<UserTitleResponse> responses = titleService.getUserTitles(1L);

		// Then
		assertEquals(1, responses.size());
	}

	@Test
	@DisplayName("getUserTitles - 실패 (존재하지 않는 유저)")
	void getUserTitles_fail_userNotFound() {
		// Given
		given(userRepository.findByIdAndStatus(100L, UserStatus.ACTIVE)).willReturn(Optional.empty());

		// When & Then
		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.getUserTitles(100L));
		assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}
}