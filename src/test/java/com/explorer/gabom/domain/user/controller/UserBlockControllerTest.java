package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.user.dto.response.UserBlockResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.service.UserBlockService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserBlockControllerTest {

    private static final Long BLOCKER_ID = 1L;
    private static final Long BLOCKED_ID = 2L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBlockService userBlockService;

    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // User 생성 및 ID 설정
        User user = new User();
        ReflectionTestUtils.setField(user, "id", BLOCKER_ID);

        // CustomUserDetails 생성자 강제 접근
        Constructor<CustomUserDetails> constructor = CustomUserDetails.class.getDeclaredConstructor(
                Long.class, String.class, String.class, UserRole.class, User.class
        );
        constructor.setAccessible(true); // private 생성자 열기

        CustomUserDetails userDetails = constructor.newInstance(
                BLOCKER_ID,
                "user@example.com",
                "password123!",
                UserRole.USER,
                user
        );

        // 인증 토큰 설정
        authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void blockUser_Success() throws Exception {
        // given
        UserBlockResponse response = new UserBlockResponse(BLOCKER_ID, BLOCKED_ID);
        when(userBlockService.blockUser(BLOCKER_ID, BLOCKED_ID)).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/block/{userId}", BLOCKED_ID)
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유저를 차단했습니다."))
                .andExpect(jsonPath("$.data.blockerId").value(BLOCKER_ID))
                .andExpect(jsonPath("$.data.blockedId").value(BLOCKED_ID));

        verify(userBlockService).blockUser(BLOCKER_ID, BLOCKED_ID);
    }

    @Test
    void unblockUser_Success() throws Exception {
        // given
        doNothing().when(userBlockService).unblockUser(BLOCKER_ID, BLOCKED_ID);

        // when & then
        mockMvc.perform(delete("/api/users/unblock/{userId}", BLOCKED_ID)
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유저 차단을 해제했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userBlockService).unblockUser(BLOCKER_ID, BLOCKED_ID);
    }
}
