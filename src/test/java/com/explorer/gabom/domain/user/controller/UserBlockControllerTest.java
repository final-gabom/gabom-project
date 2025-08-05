package com.explorer.gabom.domain.user.controller;

import com.explorer.gabom.domain.user.dto.response.UserBlockResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.service.UserBlockService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserBlockControllerTest {

    private static final Long BLOCKER_ID = 1L;
    private static final Long BLOCKED_ID = 2L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBlockService userBlockService;

    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authToken = createAuthToken(BLOCKER_ID, "user@example.com");
    }

    @Test
    public void blockUser_Success() throws Exception {
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

        // ArgumentCaptor로 검증
        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        verify(userBlockService).blockUser(captor1.capture(), captor2.capture());

        assertThat(captor1.getValue()).isEqualTo(BLOCKER_ID);
        assertThat(captor2.getValue()).isEqualTo(BLOCKED_ID);
    }

    @Test
    public void unblockUser_Success() throws Exception {
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

        verify(userBlockService, times(1)).unblockUser(BLOCKER_ID, BLOCKED_ID);
    }

    // === 유틸 ===
    private UsernamePasswordAuthenticationToken createAuthToken(Long id, String email) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);

        CustomUserDetails userDetails = new CustomUserDetails(
                id,
                email,
                "password",
                UserRole.USER,
                user
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
