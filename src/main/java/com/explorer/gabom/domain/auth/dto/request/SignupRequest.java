package com.explorer.gabom.domain.auth.dto.request;

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
	@NotBlank
	@Email
	private String email;
	@NotBlank
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하만 가능합니다.")
	private String nickname;
	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "비밀번호는 최소 8글자 이상, 대소문자 하나 이상 포함해야합니다.")
	private String password;
	@NotBlank
	private String address;
	@NotNull
	private Double lat;
	@NotNull
	private Double lng;
	@NotBlank
	private String role;
}
