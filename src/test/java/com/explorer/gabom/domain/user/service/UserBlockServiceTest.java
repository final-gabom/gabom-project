package com.explorer.gabom.domain.user.service;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserBlockServiceTest {

    private static final Long USER1_ID = 1L;
    private static final Long USER2_ID = 2L;

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

        activeUser1 = createUser(USER1_ID, "user1", "user1@example.com");
        activeUser2 = createUser(USER2_ID, "user2", "user2@example.com");
    }

    // 유틸 메서드: User 생성 분리
    private User createUser(Long id, String nickname, String email) {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .userRole(null)
                .build();
    }

    // 공통적으로 많이 쓰는 mock 설정 메서드 예시 (필요시 추가)
    private void mockActiveUsers() {
        when(userRepository.findByIdAndStatus(USER1_ID, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
        when(userRepository.findByIdAndStatus(USER2_ID, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser2));
    }

    @Nested
    @DisplayName("blockUser 메서드 테스트")
    class BlockUserTests {

        @Test
        @DisplayName("성공: 정상적으로 차단")
        void blockUser_Success() {
            mockActiveUsers();
            when(userBlockRepository.existsByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(false);

            UserBlock savedUserBlock = new UserBlock(activeUser1, activeUser2);
            when(userBlockRepository.save(any(UserBlock.class))).thenReturn(savedUserBlock);

            UserBlockResponse response = userBlockService.blockUser(USER1_ID, USER2_ID);

            assertThat(response.getBlockerId()).isEqualTo(USER1_ID);
            assertThat(response.getBlockedId()).isEqualTo(USER2_ID);

            verify(userBlockRepository).save(any(UserBlock.class));
        }

        @Test
        @DisplayName("실패: 자기 자신 차단 시도")
        void blockUser_Fail_BlockSelf() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(USER1_ID, USER1_ID);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_BLOCK_SELF);
        }

        @Test
        @DisplayName("실패: 차단 대상 사용자 없음")
        void blockUser_Fail_BlockedUserNotFound() {
            when(userRepository.findByIdAndStatus(USER1_ID, UserStatus.ACTIVE)).thenReturn(Optional.of(activeUser1));
            when(userRepository.findByIdAndStatus(USER2_ID, UserStatus.ACTIVE)).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(USER1_ID, USER2_ID);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 이미 차단된 사용자")
        void blockUser_Fail_AlreadyBlocked() {
            mockActiveUsers();
            when(userBlockRepository.existsByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(true);

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.blockUser(USER1_ID, USER2_ID);
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
            mockActiveUsers();

            UserBlock userBlock = new UserBlock(activeUser1, activeUser2);
            when(userBlockRepository.findByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(Optional.of(userBlock));

            userBlockService.unblockUser(USER1_ID, USER2_ID);

            verify(userBlockRepository).delete(userBlock);
        }

        @Test
        @DisplayName("실패: 자기 자신 차단 해제 시도")
        void unblockUser_Fail_UnblockSelf() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.unblockUser(USER1_ID, USER1_ID);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_BLOCK_SELF);
        }

        @Test
        @DisplayName("실패: 차단 기록 없음")
        void unblockUser_Fail_NotBlocked() {
            mockActiveUsers();
            when(userBlockRepository.findByBlockerAndBlocked(activeUser1, activeUser2)).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> {
                userBlockService.unblockUser(USER1_ID, USER2_ID);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_BLOCKED_USER);
        }
    }
}
