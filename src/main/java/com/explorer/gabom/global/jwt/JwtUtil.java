package com.explorer.gabom.global.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.user.type.UserRole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshTokenExpiration;

	private Key key;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String createToken(Map<String, Object> claims, Long userId, long expiration) {
		Date now = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.setClaims(claims)
				.setExpiration(new Date(now.getTime() + expiration))
				.setIssuedAt(now) // 발급일
				.signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘
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
}
