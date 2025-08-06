package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
	private static final String NEW_NICKNAME  = "newNick";
	private static final String NEW_ADDRESS   = "서울시 강남구";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TitleRepository titleRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private User testUser;
	private UsernamePasswordAuthenticationToken authToken;
	private Long savedTitleId;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();

		// 테스트용 사용자 생성 및 저장
		testUser = User.builder()
					   .email("testuser@example.com")
					   .nickname("oldNick")
					   .password("{noop}password")
					   .userRole(UserRole.USER)
					   .build();
		testUser = userRepository.save(testUser);

		// 테스트용 칭호 저장
		Title testTitle = new Title("테스트칭호", "테스트용 칭호입니다.");
		testTitle = titleRepository.save(testTitle);
		savedTitleId = testTitle.getId();

		// 인증 토큰 준비
		CustomUserDetails principal = CustomUserDetails.from(testUser);
		authToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
	}

	@DisplayName("칭호 변경 성공")
	@Test
	void updateTitle_Success() throws Exception {
		UpdateMainTitleRequest request = new UpdateMainTitleRequest(savedTitleId);

		mockMvc.perform(patch("/api/users/me/titles")
							.with(authentication(authToken))
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("칭호를 변경하였습니다."))
			   .andExpect(jsonPath("$.data.titleId").value(savedTitleId));
	}

	@DisplayName("프로필 수정 성공")
	@Test
	void updateProfile_Success() throws Exception {
		UserUpdateRequest updateRequest = new UserUpdateRequest(
			NEW_NICKNAME,
			NEW_ADDRESS,
			null,
			null,
			null
		);

		mockMvc.perform(patch("/api/users/me")
							.with(authentication(authToken))
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(updateRequest)))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("프로필 수정을 완료하였습니다."))
			   .andExpect(jsonPath("$.data.nickname").value(NEW_NICKNAME))
			   .andExpect(jsonPath("$.data.address").value(NEW_ADDRESS));
	}
}
