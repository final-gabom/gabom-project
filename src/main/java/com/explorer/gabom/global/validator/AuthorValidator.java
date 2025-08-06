package com.explorer.gabom.global.validator;

import org.springframework.stereotype.Component;

import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@Component
public class AuthorValidator {
	/**
	 * 현재 사용자가 리소스 작성자인지 검증
	 *
	 * @param resourceOwnerId 리소스를 작성한 사용자 ID
	 * @param currentUserId 현재 로그인한 사용자 ID
	 * @throws CustomException 작성자가 아닌 경우 NOT_RESOURCE_OWNER 코드가 담김 에러 리턴
	 */
	public void validateOwner(Long resourceOwnerId, Long currentUserId) {
		if (!resourceOwnerId.equals(currentUserId)) {
			throw new CustomException(ErrorCode.NOT_RESOURCE_OWNER);
		}
	}
}