package com.explorer.gabom.domain.title.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DisplayName("AdminTitleController - 통합 테스트")
@AutoConfigureMockMvc
class AdminTitleControllerTest {

	private static final Long VALID_ID = 1L;
	private static final Long INVALID_ID = 999L;

	private static final String TITLE_NAME = "차도남/녀";
	private static final String TITLE_DESCRIPTION = "서울 10곳 이상 탐험";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TitleService titleService;

	@MockBean
	private JwtProvider jwtProvider;

	@Autowired
	private ObjectMapper objectMapper;

	private TitleCreateRequest validCreateRequest;
	private TitleUpdateRequest validUpdateRequest;

	@BeforeEach
	void setUp() {
		validCreateRequest = new TitleCreateRequest(TITLE_NAME, TITLE_DESCRIPTION);
		validUpdateRequest = new TitleUpdateRequest("응애 탐험가", "누적 탐험 10km");
	}

	private String toJson(Object obj) throws JsonProcessingException {
		return objectMapper.writeValueAsString(obj);
	}

	@Nested
	@DisplayName("칭호 등록 API [POST /api/admin/titles]")
	class CreateTitle {

		@Test
		@DisplayName("성공 - 올바른 요청 시 201 상태코드와 데이터 반환")
		@WithMockUser(roles = "ADMIN")
		void createTitle_success() throws Exception {
			// given
			TitleCreateResponse response = new TitleCreateResponse(
				VALID_ID, TITLE_NAME, TITLE_DESCRIPTION, LocalDateTime.now());

			given(titleService.createTitle(any(TitleCreateRequest.class)))
				.willReturn(response);

			// when & then
			mockMvc.perform(post("/api/admin/titles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(validCreateRequest)))
				   .andExpect(status().isCreated())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 등록되었습니다."))
				   .andExpect(jsonPath("$.data.name").value(TITLE_NAME));
		}

		@Test
		@DisplayName("실패 - 이름이 비어 있을 경우 400 반환")
		@WithMockUser(roles = "ADMIN")
		void createTitle_fail_validation() throws Exception {
			TitleCreateRequest invalidRequest = new TitleCreateRequest("", "없음");

			mockMvc.perform(post("/api/admin/titles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(invalidRequest)))
				   .andExpect(status().isBadRequest())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").exists());
		}
	}

	@Nested
	@DisplayName("칭호 수정 API [PATCH /api/admin/titles/{id}]")
	class UpdateTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 수정됨")
		@WithMockUser(roles = "ADMIN")
		void updateTitle_success() throws Exception {
			mockMvc.perform(patch("/api/admin/titles/{id}", VALID_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(validUpdateRequest)))
				   .andExpect(status().isOk())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 수정되었습니다."));

			ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
			ArgumentCaptor<TitleUpdateRequest> reqCaptor = ArgumentCaptor.forClass(TitleUpdateRequest.class);

			then(titleService).should().updateTitle(idCaptor.capture(), reqCaptor.capture());

			assertThat(idCaptor.getValue()).isEqualTo(VALID_ID);
			assertThat(reqCaptor.getValue().getName()).isEqualTo("응애 탐험가");
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		@WithMockUser(roles = "ADMIN")
		void updateTitle_fail_notFound() throws Exception {
			doThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND))
				.when(titleService).updateTitle(eq(INVALID_ID), any());

			mockMvc.perform(patch("/api/admin/titles/{id}", INVALID_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(validUpdateRequest)))
				   .andExpect(status().isNotFound())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").value("해당 칭호를 찾을 수 없습니다."));
		}
	}

	@Nested
	@DisplayName("칭호 삭제 API [DELETE /api/admin/titles/{id}]")
	class DeleteTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 삭제됨")
		@WithMockUser(roles = "ADMIN")
		void deleteTitle_success() throws Exception {
			given(titleService.deleteTitle(VALID_ID)).willReturn(new TitleDeleteResponse(VALID_ID));

			mockMvc.perform(delete("/api/admin/titles/{id}", VALID_ID))
				   .andExpect(status().isOk())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 삭제되었습니다."))
				   .andExpect(jsonPath("$.data.id").value(VALID_ID));

			then(titleService).should().deleteTitle(VALID_ID);
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		@WithMockUser(roles = "ADMIN")
		void deleteTitle_fail_notFound() throws Exception {
			given(titleService.deleteTitle(INVALID_ID))
				.willThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND));

			mockMvc.perform(delete("/api/admin/titles/{id}", INVALID_ID))
				   .andExpect(status().isNotFound())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").value("해당 칭호를 찾을 수 없습니다."));
		}
	}
}
