package com.explorer.gabom.domain.auth.dto.request;

import com.explorer.gabom.domain.user.type.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignupRequest {
	@NotBlank(message = "이메일 입력은 필수입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;
	@NotBlank(message = "닉네임 입력은 필수입니다.")
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하만 가능합니다.")
	private String nickname;
	@NotBlank(message = "비밀번호 입력은 필수입니다.")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "비밀번호는 최소 8글자 이상, 대소문자 하나 이상 포함해야합니다.")
	private String password;
	@NotNull(message = "사용자, 관리자를 선택해주세요.")
	private UserRole role;
}
