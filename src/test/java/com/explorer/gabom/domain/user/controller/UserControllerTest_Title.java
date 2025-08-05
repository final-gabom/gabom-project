package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.user.dto.request.UpdateMainTitleRequest;
import com.explorer.gabom.domain.user.entity.User;
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
public class UserControllerTest_Title {

    private static final Long TEST_USER_ID = 1L;
    private static final Long NEW_TITLE_ID = 1L;
    private static final String TEST_USER_EMAIL = "testuser@example.com";
    private static final String TEST_USER_NICKNAME = "oldNick";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        authToken = createAuthToken(testUser);
    }

    @DisplayName("칭호 변경 성공")
    @Test
    void updateTitle_Success() throws Exception {
        UpdateMainTitleRequest request = new UpdateMainTitleRequest(NEW_TITLE_ID);

        mockMvc.perform(patch("/api/users/me/titles")
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("칭호를 변경하였습니다."))
                .andExpect(jsonPath("$.data.titleId").value(NEW_TITLE_ID));
    }

    // === 유틸 메서드 ===
    private User createTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .email(TEST_USER_EMAIL)
                .nickname(TEST_USER_NICKNAME)
                .userRole(UserRole.USER)
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthToken(User user) {
        CustomUserDetails principal = CustomUserDetails.fromUser(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
