package com.explorer.gabom.global.security.jwt;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		// 1) 정적/WS/문서 경로는 인증 필터 스킵
		String uri = request.getRequestURI();
		if (uri.startsWith("/ws")
			|| "/favicon.ico".equals(uri)
			|| uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")
			|| uri.endsWith(".html") || uri.endsWith(".css") || uri.endsWith(".js")
			|| uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".svg") || uri.endsWith(".ico")
			|| "/error".equals(uri)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2) 헤더에서 토큰 추출
		String token = this.getJwtFromRequest(request);

		// 3) 토큰 없으면 그냥 통과 (보호된 API는 이후 Security가 401 처리)
		if (!StringUtils.hasText(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 4) 토큰 처리만 try/catch로 감싸고, 체인 호출은 밖으로 뺀다
		try {
			log.debug("🔐 JWT 인증 필터 시작");

			// 유효하지 않으면 CustomException을 던짐(아래 catch에서 응답 종료)
			jwtProvider.validateToken(token);

			// 유효 → 인증 세팅
			String userId = jwtProvider.getUserIdFromToken(token);
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			log.debug("✅ 인증 완료 - userId: {}", userId);

		} catch (CustomException e) {
			// 토큰 이슈만 여기서 응답하고 종료
			log.warn("[JWT-CUSTOM] code={}, msg={}", e.getErrorCode(), e.getMessage(), e);
			setErrorResponse(response, e.getErrorCode());
			return;
		} catch (Exception e) {
			log.error("[JWT-ERROR] {}", e.getMessage(), e);
			setErrorResponse(response, INTERNAL_SERVER_ERROR);
			return;
		}

		// 5) 다운스트림 예외는 전역 예외 핸들러가 처리하도록 넘김
		filterChain.doFilter(request, response);
	}

	/**
	 * 요청 헤더에서 JWT 토큰을 추출합니다.
	 * "Authorization" 헤더가 존재하지만 "Bearer "로 시작하지 않으면 CustomException 발생.
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		// Authorization 헤더가 아예 없는 경우
		if (!StringUtils.hasText(bearerToken)) {
			return null;
		}

		// 형식이 잘못된 경우 → 예외 발생
		if (!bearerToken.startsWith("Bearer ")) {
			throw new CustomException(INVALID_TOKEN);
		}

		// 정상적인 Bearer 토큰이면 파싱
		return bearerToken.substring(7);
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json;charset=UTF-8");

		ApiResponse<Void> apiResponse = ApiResponse.fail(errorCode);
		String json = objectMapper.writeValueAsString(apiResponse);

		response.getWriter().write(json);
	}
}
