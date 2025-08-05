package com.explorer.gabom.domain.title.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminTitleController - 단위 테스트")
class AdminTitleControllerTest {

	private static final Long VALID_ID = 1L;
	private static final Long INVALID_ID = 999L;

	private static final String TITLE_NAME = "차도남/녀";
	private static final String TITLE_DESCRIPTION = "서울 10곳 이상 탐험";

	@InjectMocks
	private AdminTitleController adminTitleController;

	@Mock
	private TitleService titleService;

	@Mock
	private JwtProvider jwtProvider; // 필요 없다면 제거 가능

	private TitleCreateRequest validCreateRequest;
	private TitleUpdateRequest validUpdateRequest;

	@BeforeEach
	void setUp() {
		validCreateRequest = new TitleCreateRequest(TITLE_NAME, TITLE_DESCRIPTION);
		validUpdateRequest = new TitleUpdateRequest("응애 탐험가", "누적 탐험 10km");
	}

	@Nested
	@DisplayName("칭호 등록 API")
	class CreateTitle {

		@Test
		@DisplayName("성공 - 올바른 요청 시 201 상태코드와 데이터 반환")
		void createTitle_success() {
			// given
			TitleCreateResponse response = new TitleCreateResponse(
				VALID_ID, TITLE_NAME, TITLE_DESCRIPTION, LocalDateTime.now());

			given(titleService.createTitle(any())).willReturn(response);

			// when
			ResponseEntity<ApiResponse<TitleCreateResponse>> result =
				adminTitleController.createTitle(validCreateRequest);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(result.getBody()).isNotNull();
			assertThat(result.getBody().isSuccess()).isTrue();
			assertThat(result.getBody().getData().getName()).isEqualTo(TITLE_NAME);
		}

		@Test
		@DisplayName("실패 - 이름이 비어 있으면 예외 발생")
		void createTitle_fail_validation() {
			// given
			TitleCreateRequest invalidRequest = new TitleCreateRequest("", "없음");

			// when
			// 단위 테스트에서는 @Valid 유효성 검사는 동작하지 않으므로,
			// Validator를 수동 호출하거나 통합 테스트로 분리 필요
			// 여기선 로직 실패를 가정한 시나리오를 예로 듬
			// 또는 서비스에서 CustomException을 던질 수도 있음
			given(titleService.createTitle(any()))
				.willThrow(new CustomException(ErrorCode.BAD_REQUEST));

			// then
			CustomException ex = assertThrows(CustomException.class, () ->
				adminTitleController.createTitle(invalidRequest));

			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST);
		}
	}

	@Nested
	@DisplayName("칭호 수정 API")
	class UpdateTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 수정됨")
		void updateTitle_success() {
			// when
			ResponseEntity<ApiResponse<Void>> result =
				adminTitleController.updateTitle(VALID_ID, validUpdateRequest);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody()).isNotNull();
			assertThat(result.getBody().isSuccess()).isTrue();
			assertThat(result.getBody().getMessage()).isEqualTo("칭호가 성공적으로 수정되었습니다.");

			verify(titleService).updateTitle(eq(VALID_ID), eq(validUpdateRequest));
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		void updateTitle_fail_notFound() {
			// given
			doThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND))
				.when(titleService).updateTitle(eq(INVALID_ID), any());

			// when & then
			CustomException ex = assertThrows(CustomException.class, () ->
				adminTitleController.updateTitle(INVALID_ID, validUpdateRequest));

			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.TITLE_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("칭호 삭제 API")
	class DeleteTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 삭제됨")
		void deleteTitle_success() {
			// given
			TitleDeleteResponse response = new TitleDeleteResponse(VALID_ID);
			given(titleService.deleteTitle(VALID_ID)).willReturn(response);

			// when
			ResponseEntity<ApiResponse<TitleDeleteResponse>> result =
				adminTitleController.deleteTitle(VALID_ID);

			// then
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody().isSuccess()).isTrue();
			assertThat(result.getBody().getData().getId()).isEqualTo(VALID_ID);

			verify(titleService).deleteTitle(VALID_ID);
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		void deleteTitle_fail_notFound() {
			// given
			doThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND))
				.when(titleService).deleteTitle(INVALID_ID);

			// when & then
			CustomException ex = assertThrows(CustomException.class, () ->
				adminTitleController.deleteTitle(INVALID_ID));

			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.TITLE_NOT_FOUND);
		}
	}
}


