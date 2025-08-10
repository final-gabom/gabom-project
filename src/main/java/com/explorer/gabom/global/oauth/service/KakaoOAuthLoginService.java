package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.type.OAuthProvider;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoOAuthLoginService implements SocialOAuthLoginService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    @Override
    public SocialLoginResponse login(String code) {
        // 1. 인가 코드로 액세스 토큰 받기
        String accessToken = getAccessToken(code);

        // 2. 액세스 토큰으로 사용자 정보 조회
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                String.class
        );
        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            Long providerId = root.path("id").asLong();
            String email = root.path("kakao_account").path("email").asText();

            // 3. 사용자 DB 조회 후 없으면 예외처리
            User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // 4. JWT 토큰 발급
            String issuedAccessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
            String issuedRefreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

            // 5. Redis에 refresh token 저장
            redisTokenService.saveRefreshToken(user.getId(), issuedRefreshToken, jwtProvider.getRefreshTokenExpiration());

            // 6. 로그인 응답 생성 및 반환
            return SocialLoginResponse.builder()
                    .providerId(String.valueOf(providerId))
                    .email(email)
                    .accessToken(issuedAccessToken)
                    .refreshToken(issuedRefreshToken)
                    .build();

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }

    // 인가 코드로 액세스 토큰 요청 메서드
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        try {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            return root.path("access_token").asText();
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 중 오류 발생", e);
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }
}
