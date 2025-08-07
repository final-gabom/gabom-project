package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.type.OAuthProvider;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.explorer.gabom.global.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoOAuthLoginService implements SocialOAuthLoginService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

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

            User user = userRepository.findByEmailAndStatus(email,UserStatus.ACTIVE)
                    .orElseGet(() -> userRepository.save(
                            User.builder()
                                    .email(email)
                                    .nickname("kakao_" + providerId) // 기본 닉네임 임의 지정
                                    .password("")  // 소셜 로그인은 비밀번호 필요 없거나 빈 문자열 처리
                                    .userRole(UserRole.USER)
                                    .build()
                    ));

            String issuedAccessToken = jwtProvider.createAccessToken(user.getId(),user.getUserRole());
            String issuedRefreshToken = jwtProvider.createRefreshToken(user.getId(),user.getUserRole());

            return SocialLoginResponse.builder()
                    .providerId(String.valueOf(providerId))
                    .email(email)
                    .accessToken(issuedAccessToken)
                    .refreshToken(issuedRefreshToken)
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
