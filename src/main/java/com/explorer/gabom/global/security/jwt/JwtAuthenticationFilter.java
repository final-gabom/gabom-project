package com.explorer.gabom.global.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UserDetailsService userDetailsService;

	/**
	 * HTTP 요청을 처리하는 필터로, 요청에 포함된 JWT 토큰을 검증하고 유효한 경우 인증 정보를 SecurityContext에 설정합니다.
	 * <p>
	 * 이 메서드는 Spring Security에서 요청을 처리하기 전에 호출되며, 사용자의 인증 상태를 설정합니다.
	 * 유효한 JWT 토큰이 있을 경우, 해당 토큰에서 사용자 ID를 추출하고, 이를 통해 사용자의 정보를 로드한 후 인증 정보를 설정합니다.
	 * </p>
	 *
	 * @param request HTTP 요청 객체
	 * @param response HTTP 응답 객체
	 * @param filterChain 필터 체인, 다음 필터를 호출할 때 사용
	 * @throws ServletException 서블릿 처리 중 발생한 예외
	 * @throws IOException 입출력 관련 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		// 요청에서 JWT 토큰을 추출
		String token = this.getJwtFromRequest(request);

		// 토큰이 유효한지 검증
		if (jwtProvider.validateToken(token)) {
			// 유효한 토큰이라면, 토큰에서 사용자 ID를 추출
			String userId = jwtProvider.getUserIdFromToken(token);
			// 사용자 ID를 기반으로 사용자 정보를 로드
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
			// 사용자 정보와 권한을 기반으로 인증 토큰 생성
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
			// 인증 토큰에 요청 정보를 설정 (추후 세션에서 사용할 수 있도록 설정)
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// SecurityContext에 인증 정보를 설정하여 후속 요청에서 사용할 수 있도록 함
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * HTTP 요청에서 JWT 토큰을 추출하는 메서드입니다.
	 * <p>
	 * Authorization 헤더에서 "Bearer"로 시작하는 토큰을 추출하여 반환합니다.
	 * 만약 "Bearer"로 시작하지 않거나 헤더가 없으면 null을 반환합니다.
	 * </p>
	 *
	 * @param request HTTP 요청 객체
	 * @return JWT 토큰 문자열, 없으면 null
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
