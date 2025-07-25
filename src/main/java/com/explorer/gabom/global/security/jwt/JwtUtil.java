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
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

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

	public String createToken(Map<String, Object> claims, Long userId, long expiration) {
		Date now = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.subject(String.valueOf(userId))
				.claims(claims)
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.issuedAt(now)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

	}

	public String createAccessToken(Long userId, UserRole userRole) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userRole);
		claims.put("type", "ACCESS");
		return createToken(claims, userId, accessTokenExpiration);
	}

	public String createRefreshToken(Long userId, UserRole userRole) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userRole);
		claims.put("type", "REFRESH");
		return createToken(claims, userId, refreshTokenExpiration);
	}

	public Claims getClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		throw new CustomException(ErrorCode.INVALID_TOKEN_VALUE);
	}

	public boolean validateToken(String token) {
		try {
			if (StringUtils.hasText(token)) {
				Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token);
				return true;
			}
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token : {}", token, ex);
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token : {}", token, ex);
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token : {}", token, ex);
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty. : {}", token, ex);
		} catch (Exception ex) {
			log.error("Invalid JWT token : {}", token, ex);
		}
		return false;
	}

}
