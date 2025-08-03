package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        String uniqueSuffix = String.valueOf(System.nanoTime());

        testUser = User.builder()
                .nickname("oldNick" + uniqueSuffix)
                .email("test" + uniqueSuffix + "@example.com")
                .password("{noop}password")
                .userRole(UserRole.USER)
                .build();

        userRepository.save(testUser);
    }

    @Test
    void 프로필_수정_성공() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "newNick",
                "서울시 강남구",
                null,
                null,
                null
        );

        CustomUserDetails principal = CustomUserDetails.fromUser(testUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        mockMvc.perform(patch("/api/users/me")  // 실제 컨트롤러 매핑 경로 맞추세요
                        .with(authentication(authToken))  // 인증 정보 직접 주입
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("프로필 수정을 완료하였습니다."))
                .andExpect(jsonPath("$.data.nickname").value("newNick"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구"));
    }
}
