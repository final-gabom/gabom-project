package com.explorer.gabom.domain.social.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.SsoAuthToken;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KAKAO")
@RequiredArgsConstructor
public class KakaoService implements SocialService {

	private final RestTemplate restTemplate;

	@Value("${KAKAO_CLIENT_ID}")
	private String kakaoClientId;

	@Value("${KAKAO_REDIRECT_URI}")
	private String kakaoRedirectUri;

	@Override
	public SocialProvider getProvider() {
		return SocialProvider.KAKAO;
	}

	@Override
	public String getAuthorizationUrl() {
		return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
								   .queryParam("client_id", kakaoClientId)
								   .queryParam("redirect_uri", kakaoRedirectUri)
								   .queryParam("response_type", "code")
								   .toUriString();
	}

	// 액세스 토큰을 사용해 카카오에서 사용자 정보를 조회하는 메서드
	@Override
	public OAuthUserInfo getOAuthUserInfoForProvider(String accessToken) {
		String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		try {
			// 사용자 정보 요청
			ResponseEntity<String> response = restTemplate.exchange(
				userInfoUrl,
				HttpMethod.GET,
				request,
				String.class
			);
			// 여기 로그 추가 (사용자 정보 응답 출력)
			log.debug("카카오 사용자 정보 응답: {}", response.getBody());
			// 응답 JSON 파싱
			JsonNode root = new ObjectMapper().readTree(response.getBody());
			Long providerId = root.path("id").asLong();
			String email = root.path("kakao_account").path("email").asText(null);
			// 이메일 없으면 에러 처리
			if (email == null) {
				throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR);
			}

			return new OAuthUserInfo(SocialProvider.KAKAO, String.valueOf(providerId), email);

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
