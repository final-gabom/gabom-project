package com.explorer.gabom.global.validator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordValidator {
	private final PasswordEncoder encoder;

	/**
	 * 입력된 비밀번호가 기존(저장된) 비밀번호와 일치하는지 검증합니다.
	 *
	 * <p>비밀번호 비교에는 {@link PasswordEncoder#matches(CharSequence, String)} 메서드를 사용하며,
	 * 입력된 비밀번호를 암호화하여 기존 암호화된 비밀번호와 비교합니다.</p>
	 *
	 * <p>비밀번호가 일치하지 않을 경우 {@link com.explorer.gabom.global.exception.CustomException} 예외를 발생시키며,
	 * 일반적으로 인증 실패 또는 접근 거부 상황에서 사용됩니다.</p>
	 *
	 * @param inputPassword 사용자가 입력한 원문 비밀번호
	 * @param originalPassword DB 등에 저장된 암호화된 비밀번호
	 * @throws CustomException 비밀번호가 일치하지 않을 경우 발생
	 */
	public void verifyMatch(String inputPassword, String originalPassword) {
		if (!encoder.matches(inputPassword, originalPassword)) {
			throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
		}
	}

}
