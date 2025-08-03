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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserBlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBlockService userBlockService;

    private CustomUserDetails customUserDetails;

    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // User 엔티티 필드 id 직접 세팅 (private 필드라 리플렉션 사용)
        User mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        customUserDetails = new CustomUserDetails(
                1L,
                "user@example.com",
                "password",
                UserRole.USER,
                mockUser
        );

        authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    public void blockUser_Success() throws Exception {
        UserBlockResponse response = new UserBlockResponse(1L, 2L);
        when(userBlockService.blockUser(1L, 2L)).thenReturn(response);

        mockMvc.perform(post("/api/users/block/{userId}", 2L)
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유저를 차단했습니다."))
                .andExpect(jsonPath("$.data.blockerId").value(1))
                .andExpect(jsonPath("$.data.blockedId").value(2));

        verify(userBlockService, times(1)).blockUser(1L, 2L);
    }

    @Test
    public void unblockUser_Success() throws Exception {
        doNothing().when(userBlockService).unblockUser(1L, 2L);

        mockMvc.perform(delete("/api/users/unblock/{userId}", 2L)
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("유저 차단을 해제했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userBlockService, times(1)).unblockUser(1L, 2L);
    }
}