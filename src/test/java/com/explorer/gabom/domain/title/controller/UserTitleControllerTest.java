package com.explorer.gabom.domain.title.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserTitleController - 단위 테스트")
class UserTitleControllerTest {

	private static final Long VALID_USER_ID = 1L;
	private static final Long INVALID_USER_ID = 999L;

	@InjectMocks
	private UserTitleController userTitleController;

	@Mock
	private TitleService titleService;

	@Mock
	private JwtProvider jwtProvider; // 실제 사용하지 않으면 생략 가능

	private List<UserTitleResponse> mockResponse;

	@BeforeEach
	void setUp() {
		mockResponse = List.of(
			new UserTitleResponse(10L, "팀험가", "첫 칭호"),
			new UserTitleResponse(11L, "만렙탐험가", "많이 돌아다님")
		);
	}

	@Test
	@DisplayName("성공 - 유저의 칭호 목록 조회")
	void getUserTitles_success() {
		// given
		given(titleService.getUserTitles(VALID_USER_ID)).willReturn(mockResponse);

		// when
		ResponseEntity<ApiResponse<List<UserTitleResponse>>> response =
			userTitleController.getUserTitles(VALID_USER_ID);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		ApiResponse<List<UserTitleResponse>> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.isSuccess()).isTrue();
		assertThat(body.getMessage()).isEqualTo("칭호가 성공적으로 조회되었습니다.");
		assertThat(body.getData()).hasSize(2);
		assertThat(body.getData().get(0).getId()).isEqualTo(10L);
		assertThat(body.getData().get(0).getName()).isEqualTo("팀험가");

		// verify 호출 인자
		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		then(titleService).should().getUserTitles(captor.capture());
		assertThat(captor.getValue()).isEqualTo(VALID_USER_ID);
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 유저의 칭호 목록 조회")
	void getUserTitles_fail_notFound() {
		// given
		given(titleService.getUserTitles(INVALID_USER_ID))
			.willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		// when & then
		CustomException ex = assertThrows(CustomException.class, () ->
			userTitleController.getUserTitles(INVALID_USER_ID));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

		then(titleService).should().getUserTitles(INVALID_USER_ID);
	}
}

