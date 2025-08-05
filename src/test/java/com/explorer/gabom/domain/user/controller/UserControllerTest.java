package com.explorer.gabom.domain.user.controller;

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
public class UserControllerTest {

    private static final String OLD_NICKNAME_PREFIX = "oldNick";
    private static final String EMAIL_DOMAIN = "@example.com";
    private static final String NEW_NICKNAME = "newNick";
    private static final String NEW_ADDRESS = "서울시 강남구";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = createTestUser();
        userRepository.save(testUser);

        authToken = createAuthToken(testUser);
    }
    @DisplayName("프로필 수정 성공")
    @Test
    void updateProfile_success() throws Exception {
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

    // === 유틸 메서드 ===
    private User createTestUser() {
        String uniqueSuffix = String.valueOf(System.nanoTime());
        return User.builder()
                .nickname(OLD_NICKNAME_PREFIX + uniqueSuffix)
                .email("test" + uniqueSuffix + EMAIL_DOMAIN)
                .password("{noop}password")
                .userRole(UserRole.USER)
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthToken(User user) {
        CustomUserDetails principal = CustomUserDetails.fromUser(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
