package com.explorer.gabom.domain.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 로컬/테스트 환경에서만 동작하는 관리자 계정 시더(부팅 시 자동 실행).
 * - email = "admin@test" 가 없으면 새로 생성
 * - 있으면 아무 것도 하지 않고 로그만 남김
 */
@Profile({"local","test"})        // 운영(prod)에서 실수로 돌아가지 않게 프로필 제한
@Order(0)                         // 다른 로더들보다 가장 먼저 실행되어, admin 참조가 항상 가능하게 함
@Slf4j
@Component
@RequiredArgsConstructor
public class TestAdminSeeder implements CommandLineRunner {

	private final UserRepository userRepository; // 관리자 존재 여부 확인 및 저장

	@Override
	@Transactional // 런너 실행 동안 생성/저장을 하나의 트랜잭션으로 처리(끝나면 커밋)
	public void run(String... args) {
		// 1) 이메일로 관리자 존재 여부 확인 (email 컬럼은 unique 가정)
		userRepository.findByEmail("admin@test").ifPresentOrElse(
			// 이미 있으면: 단순히 로그만 남기고 종료
			u -> log.info("[TestAdminSeeder] admin(email) exists id={}", u.getId()),

			// 없으면: 새 관리자 계정 생성 후 저장
			() -> {
				User admin = User.builder()
								 .email("admin@test")
								 .password("{noop}admin1234")   // 로컬/테스트에서만 사용. 운영에선 반드시 인코딩 사용!
								 .nickname("ADMIN")
								 .userRole(UserRole.ADMIN)
								 .build();

				// save() 호출 시 JPA가 PK 할당 → 메서드 종료 시 @Transactional에 의해 커밋
				userRepository.save(admin);

				// 생성된 PK 확인용 로그
				log.info("[TestAdminSeeder] created admin id={}", admin.getId());
			}
		);
	}
}



