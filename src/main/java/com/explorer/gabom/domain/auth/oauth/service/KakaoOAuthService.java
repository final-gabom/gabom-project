package com.explorer.gabom.domain.auth.oauth.service;

import com.explorer.gabom.domain.auth.service.SocialLoginService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.domain.auth.oauth.dto.OAuthUserInfo;
import com.explorer.gabom.domain.auth.oauth.dto.SsoAuthToken;
import com.explorer.gabom.domain.auth.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;
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
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoOAuthService implements SocialOAuthLoginService {

    private final RestTemplate restTemplate;

    private final SocialLoginService socialLoginService;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }
    @Override
    public String getAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code")
                .toUriString();
    }

    @Override
    public SocialLoginResponse login(String code) {
        // 인가 코드로 엑세스 토큰 받기
        String accessToken = getAccessToken(code);
        // 액세스 토큰으로 카카오 사용자 정보 조회 API 호출 준비
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        // 사용자 정보 요청
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                String.class
        );
        // 여기 로그 추가 (사용자 정보 응답 출력)
        log.debug("카카오 사용자 정보 응답: {}", response.getBody());
        try {
            // 응답 JSON 파싱
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            Long providerId = root.path("id").asLong();
            String email = root.path("kakao_account").path("email").asText(null);
            // 이메일 없으면 에러 처리
            if (email == null) {
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
            }
            // 공통 사용자 정보 DTO 생성
            OAuthUserInfo userInfo = new OAuthUserInfo(OAuthProvider.KAKAO, String.valueOf(providerId), email);

            return socialLoginService.socialLogin(userInfo);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }

    // 인가 코드로 액세스 토큰 요청하는 메서드
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 파라미터 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 토큰 요청 POST
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        // 여기 로그 추가 (응답 바디 출력)
        log.debug("카카오 액세스 토큰 응답: {}", response.getBody());
        try {
            ObjectMapper mapper = new ObjectMapper();
            SsoAuthToken token = mapper.readValue(response.getBody(), SsoAuthToken.class);
            return token.getAccess_token();
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 중 오류 발생", e);
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }
}
