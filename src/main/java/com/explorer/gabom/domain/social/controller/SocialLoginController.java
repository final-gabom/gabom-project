package com.explorer.gabom.domain.social.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.social.dto.response.SignupResponse;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.service.SocialLoginService;
import com.explorer.gabom.domain.social.service.SocialLoginServiceFactory;
import com.explorer.gabom.domain.social.service.SocialService;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/social")
public class SocialLoginController {

	private final SocialLoginServiceFactory socialLoginServiceFactory;
	private final SocialLoginService socialLoginService;

	// 로그인 페이지로 이동 (리다이렉트)
	@GetMapping("/{provider}")
	public void redirectToProvider(
		@PathVariable SocialProvider provider,
		HttpServletResponse response) throws IOException {
		log.info("🔗 소셜 로그인 요청: provider={}", provider);
		// 해당 provider에 맞는 로그인 서비스 객체를 팩토리에서 가져옴
		SocialService service = socialLoginServiceFactory.getService(provider);
		// 해당 서비스에서 제공하는 소셜 로그인 URL을 가져옴 (ex. 카카오 로그인 페이지 URL)
		String authorizationUrl = service.getAuthorizationUrl();
		// 클라이언트에게 소셜 로그인 페이지 URL로 리다이렉트 응답
		log.info("➡️  {} 로그인 페이지로 리다이렉트: {}", provider, authorizationUrl);
		response.sendRedirect(authorizationUrl);
	}

	@GetMapping("/{provider}/callback")
	public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
		@PathVariable SocialProvider provider,
		@RequestParam String code) {
		log.info("🎯 {} 콜백 도착. 인가 코드 수신: {}", provider, code);
		SocialService socialService = socialLoginServiceFactory.getService(provider);
		// 1. 코드로 토큰 가져오기
		String accessToken = socialService.getAccessToken(code);
		log.debug("🔐 {} 액세스 토큰 발급 완료: {}", provider, accessToken);

		// 2. 토큰으로 사용자 정보 가져오기
		OAuthUserInfo userInfo = socialService.getOAuthUserInfoForProvider(accessToken);
		log.info("📥 {} 사용자 정보 조회 완료: {}", provider, userInfo.getEmail());

		// 3. 가져온 사용자 정보로 회원가입 여부 확인 -> 로그인을 할지 회원가입을 할지 결정
		SocialAccount socialAccount = socialLoginService.handleLogin(userInfo);
		return ResponseEntity.ok(ApiResponse.success("로그인 성공", socialLoginService.socialLogin(socialAccount)));

	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signUp(
		@Valid @RequestBody SocialSignupRequest signupRequest) {
		log.info("✍️ 소셜 회원가입 요청: tempId={}, nickname={}", signupRequest.getTempId(), signupRequest.getNickname());
		SignupResponse response = socialLoginService.socialSignUp(signupRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입 성공", response));
	}
}
