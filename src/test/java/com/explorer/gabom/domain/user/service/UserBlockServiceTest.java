package com.explorer.gabom.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.explorer.gabom.domain.user.dto.response.UserBlockResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.entity.UserBlock;
import com.explorer.gabom.domain.user.repository.UserBlockRepository;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserBlockServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBlockRepository userBlockRepository;

    private UserBlockService userBlockService;

    private User activeUser1;
    private User activeUser2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userBlockService = new UserBlockService(userRepository, userBlockRepository);

        activeUser1 = User.builder()
                .id(1L)
                .nickname("user1")
                .email("user1@example.com")
                .userRole(null)
                .build();

        activeUser2 = User.builder()
                .id(2L)
                .nickname("user2")
                .email("user2@example.com")
                .userRole(null)
                .build();
    }

    @Nested
    @DisplayName("blockUser 메서드 테스트")
    class BlockUserTests {

        @Test
        @DisplayName("성공: 정상적으로 차단")
        void blockUser_Success() {
            when(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(2L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser2));
            when(userBlockRepository.existsByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(false);

            UserBlock savedUserBlock = new UserBlock(activeUser1, activeUser2);
            when(userBlockRepository.save(any(UserBlock.class))).thenReturn(savedUserBlock);

            UserBlockResponse response = userBlockService.blockUser(1L, 2L);

            assertThat(response.getBlockerId()).isEqualTo(1L);
            assertThat(response.getBlockedId()).isEqualTo(2L);

            verify(userBlockRepository).save(any(UserBlock.class));
        }

        @Test
        @DisplayName("실패: 자기 자신 차단 시도")
        void blockUser_Fail_BlockSelf() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(1L, 1L);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_BLOCK_SELF);
        }

        @Test
        @DisplayName("실패: 차단 대상 사용자 없음")
        void blockUser_Fail_BlockedUserNotFound() {
            when(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(2L, UserStatus.ACTIVE)).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(1L, 2L);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 이미 차단된 사용자")
        void blockUser_Fail_AlreadyBlocked() {
            when(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(2L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser2));
            when(userBlockRepository.existsByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(true);

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(1L, 2L);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_BLOCKED_USER);
        }
    }

    @Nested
    @DisplayName("unblockUser 메서드 테스트")
    class UnblockUserTests {

        @Test
        @DisplayName("성공: 정상적으로 차단 해제")
        void unblockUser_Success() {
            when(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(2L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser2));

            UserBlock userBlock = new UserBlock(activeUser1, activeUser2);
            when(userBlockRepository.findByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(Optional.of(userBlock));

            userBlockService.unblockUser(1L, 2L);

            verify(userBlockRepository).delete(userBlock);
        }

        @Test
        @DisplayName("실패: 자기 자신 차단 해제 시도")
        void unblockUser_Fail_UnblockSelf() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.unblockUser(1L, 1L);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_BLOCK_SELF);
        }

        @Test
        @DisplayName("실패: 차단 기록 없음")
        void unblockUser_Fail_NotBlocked() {
            when(userRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(2L, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser2));
            when(userBlockRepository.findByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.unblockUser(1L, 2L);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_BLOCKED_USER);
        }
    }
}
