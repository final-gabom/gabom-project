package com.explorer.gabom.domain.social.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirstLoginService {
	private final StringRedisTemplate stringRedisTemplate;
	private static final String PREFIX = "user:first-login";

	/**
	 * 회원가입 직후(유저 생성 직후) 호출 : 최초 로그인 플래그 설정
	 */
	public void markFirstLogin(Long userId) {
		stringRedisTemplate.opsForValue().set(PREFIX + userId, "1");
	}

	/**
	 * 로그인 시 호출 : 키를 지우면서(DELETE) 이번이 첫 로그인인지 원자적으로 판별
	 *  - ture  : 키가 있었고 이번이 첫 로그인 -> 지우고 ture반환
	 *  - false : 이미 지워졌거나 없음 -> 첫 로그인이 아님
	 */
	public boolean consumeFirstLogin(Long userId) {
		Boolean removed = stringRedisTemplate.delete((PREFIX + userId));
		return removed;
	}
}
