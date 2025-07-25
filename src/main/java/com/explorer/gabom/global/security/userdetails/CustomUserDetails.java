package com.explorer.gabom.global.security.userdetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.type.UserRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

	private Long userId;
	private String email;
	private String password;
	private String role;

	@Builder
	private CustomUserDetails(Long userId, String email, String password, UserRole role) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.role = role.name();
	}

	public static CustomUserDetails from(User user) {
		return CustomUserDetails.builder()
								.userId(user.getId())
								.email(user.getEmail())
								.password(user.getPassword())
								.role(user.getUserRole())
								.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return "";
	}
}
