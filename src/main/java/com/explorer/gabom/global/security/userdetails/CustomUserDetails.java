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
	private User user;

	@Builder
    private CustomUserDetails(Long userId, String email, String password, UserRole role, User user) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.role = role.getValue();
		this.user = user;
	}

	public static CustomUserDetails from(User user) {
		return CustomUserDetails.builder()
								.userId(user.getId())
								.email(user.getEmail())
								.password(user.getPassword())
								.role(user.getUserRole())
								.user(user)
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
		return String.valueOf(this.userId);
	}

	public User getUser() {
		return this.user;
	}

	public static CustomUserDetails fromUser(User user) {
		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getUserRole(), user);
	}
}
