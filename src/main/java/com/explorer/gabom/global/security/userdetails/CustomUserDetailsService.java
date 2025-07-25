package com.explorer.gabom.global.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
		try {
			Long userId = Long.parseLong(subject);
			User user = userRepository.findById(userId)
									  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

			return CustomUserDetails.from(user);
		} catch (NumberFormatException e) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}
}