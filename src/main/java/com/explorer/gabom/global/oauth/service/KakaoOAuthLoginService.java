package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.type.OAuthProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoOAuthLoginService implements SocialOAuthLoginService {

    private final RestTemplate restTemplate;

    @Override
    public SocialLoginResponse login(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

// 요청 헤더 생성
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

            // ❗ accessToken과 refreshToken은 지금 단계에서는 그대로 반환 (refreshToken은 Kakao 에선 안옴)
            return SocialLoginResponse.builder()
                    .providerId(String.valueOf(providerId))
                    .email(email)
                    .accessToken(accessToken)
                    .refreshToken(null) // 이 부분은 JWT 발급 시 교체 예정
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }
    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }
}
