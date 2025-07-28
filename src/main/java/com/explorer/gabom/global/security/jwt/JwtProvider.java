package com.explorer.gabom.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	private static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshTokenExpiration;

	private SecretKey key;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * JWT 토큰을 생성합니다.
	 *
	 * <p>지정된 사용자 ID, 클레임 정보(예: 권한, 토큰 타입), 만료 시간을 기반으로 JWT 토큰을 생성하며,
	 * HMAC-SHA256 알고리즘으로 서명됩니다.</p>
	 *
	 * <p>토큰은 "Bearer " 접두사를 포함한 형태로 반환됩니다.</p>
	 *
	 * @param claims JWT에 포함될 클레임 정보 (예: role, type 등)
	 * @param userId 토큰 대상 사용자 ID (subject로 사용됨)
	 * @param expiration 토큰의 만료 시간 (밀리초 단위)
	 * @return "Bearer " 접두사가 포함된 JWT 문자열
	 */
	public String createToken(Map<String, Object> claims, Long userId, long expiration) {
		Date now = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.subject(String.valueOf(userId))
				.claims(claims)
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.issuedAt(now)
				.signWith(key)
				.compact();

	}

	/**
	 * Access Token을 생성합니다.
	 *
	 * <p>사용자의 ID와 권한 정보를 기반으로, 만료 시간이 짧은 Access Token을 생성합니다.
	 * 토큰에는 "role"과 "type=ACCESS" 클레임이 포함됩니다.</p>
	 *
	 * @param userId 로그인한 사용자 ID
	 * @param userRole 사용자 권한 (예: USER, ADMIN)
	 * @return "Bearer " 접두사가 포함된 Access Token 문자열
	 */
	public String createAccessToken(Long userId, UserRole userRole) {
		log.info("AccessToken 생성 요청 - userId: {}, role: {}", userId, userRole.name());
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userRole);
		claims.put("type", "ACCESS");

		String token = createToken(claims, userId, accessTokenExpiration);

		log.debug("AccessToken 생성 완료 - userId: {}", userId);
		return token;
	}

	/**
	 * Refresh Token을 생성합니다.
	 *
	 * <p>사용자의 ID와 권한 정보를 기반으로, 만료 시간이 긴 Refresh Token을 생성합니다.
	 * 토큰에는 "role"과 "type=REFRESH" 클레임이 포함됩니다.</p>
	 *
	 * @param userId 로그인한 사용자 ID
	 * @param userRole 사용자 권한 (예: USER, ADMIN)
	 * @return "Bearer " 접두사가 포함된 Refresh Token 문자열
	 */
	public String createRefreshToken(Long userId, UserRole userRole) {
		log.info("RefreshToken 생성 요청 - userId: {}, role: {}", userId, userRole.name());
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userRole);
		claims.put("type", "REFRESH");

		String token = createToken(claims, userId, refreshTokenExpiration);

		log.debug("RefreshToken 생성 완료 - userId: {}", userId);
		return token;
	}

	/**
	 * 주어진 JWT 토큰에서 Claims를 추출합니다.
	 *
	 * <p>내부적으로 서명을 검증하며, 서명이 유효하지 않으면 예외를 발생시킵니다.<br>
	 * 서명이 유효하고 형식이 정상적인 경우 JWT의 Claims를 반환합니다.</p>
	 *
	 * @param token "Bearer " 접두사가 제거된 순수 JWT 문자열
	 * @return JWT에 포함된 Claims (사용자 정보, 권한 등)
	 * @throws io.jsonwebtoken.JwtException 서명 검증 실패, 만료, 구조 오류 등 JWT가 유효하지 않은 경우 예외 발생
	 */
	public Claims getClaims(String token) {
		log.debug("JWT Claims 파싱 시작");
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * 주어진 JWT 토큰이 유효한 서명으로 생성되었는지 검증합니다.
	 *
	 * <p>서명 키를 기반으로 JWT를 파싱하며, 이 과정에서 다음 항목을 검사합니다:
	 * <ul>
	 *   <li>토큰 구조의 유효성</li>
	 *   <li>서명이 위조되지 않았는지</li>
	 *   <li>토큰의 만료 여부</li>
	 * </ul>
	 * </p>
	 *
	 * <p>유효한 토큰일 경우 true를 반환하고, 유효하지 않거나 예외가 발생하면 false를 반환합니다.</p>
	 *
	 * @param token "Bearer " 접두사가 제거된 순수 JWT 문자열
	 * @return 서명 및 만료 여부를 포함한 유효성 검증 결과
	 */
	public boolean validateToken(String token) {
		log.debug("JWT 유효성 검증 시작");
		try {
			if (StringUtils.hasText(token)) {
				// 1. 서명을 검증하고 파싱 시도 (예외 발생 시 false)
				Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token);
				return true;
			}
		} catch (MalformedJwtException ex) {
			// JWT 형식이 잘못된 경우 (구조가 비정상)
			log.error("Invalid JWT token : {}", token, ex);
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		} catch (ExpiredJwtException ex) {
			// 토큰이 만료된 경우
			log.error("Expired JWT token : {}", token, ex);
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		} catch (UnsupportedJwtException ex) {
			// 지원하지 않는 JWT 형식인 경우
			log.error("Unsupported JWT token : {}", token, ex);
			throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
		} catch (IllegalArgumentException ex) {
			// Claims 문자열이 비어 있거나 null인 경우
			log.error("JWT claims string is empty. : {}", token, ex);
			throw new CustomException(ErrorCode.EMPTY_TOKEN);
		} catch (Exception ex) {
			// 기타 예외 (예: 서명 실패 등)
			log.error("Invalid JWT token : {}", token, ex);
			throw new CustomException(ErrorCode.SIGNATURE_INVALID);
		}
		return false;
	}

	/**
	 * JWT 토큰에서 사용자 ID(subject)를 추출합니다.
	 *
	 * <p>토큰의 서명을 검증한 후, Payload(Claims)에서 subject 값을 읽어 반환합니다.
	 * 일반적으로 subject는 사용자 ID(PK)로 사용됩니다.</p>
	 *
	 * @param token "Bearer " 접두사가 제거된 JWT 토큰 문자열
	 * @return 토큰에 저장된 사용자 ID (subject 클레임 값)
	 * @throws io.jsonwebtoken.JwtException 서명 검증 실패 또는 형식 오류 발생 시 예외 발생
	 */
	public String getUserIdFromToken(String token) {
		log.debug("JWT에서 사용자 ID 추출 시도");
		return this.getClaims(token).getSubject();
	}

}
