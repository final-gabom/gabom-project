package com.explorer.gabom.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {

		Info info = new Info()
			.title("가봄? API Document")
			.version("1.0")
			.description(
				"환영합니다. \n"
					+ "[가봄?]은 지역 곳곳의 숨겨진 장소를 탐험하기 위해 만든 플랫폼입니다. "
					+ "이 API 문서는 [가봄?]의 API를 사용하는 방법을 설명합니다."
			);

		return new OpenAPI()
			.components(new Components())
			.info(info);
	}

	@Bean
	public GroupedOpenApi authApi() {
		return GroupedOpenApi.builder()
							 .group("auth")
							 .packagesToScan("com.explorer.gabom.domain.auth") // auth 관련 컨트롤러 있는 패키지
							 .build();
	}


	@Bean
	public GroupedOpenApi placeApi() {
		return GroupedOpenApi.builder()
							 .group("place")
							 .packagesToScan("com.explorer.gabom.domain.place") // place 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi explorationApi() {
		return GroupedOpenApi.builder()
							 .group("exploration")
							 .packagesToScan("com.explorer.gabom.domain.exploration") // exploration 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi activityApi() {
		return GroupedOpenApi.builder()
							 .group("activity")
							 .packagesToScan("com.explorer.gabom.domain.activity") // activity 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi fileApi() {
		return GroupedOpenApi.builder()
							 .group("file")
							 .packagesToScan("com.explorer.gabom.domain.file") // file 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi missionproofApi() {
		return GroupedOpenApi.builder()
							 .group("missionproof")
							 .packagesToScan("com.explorer.gabom.domain.missionproof") // missionproof 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi pointApi() {
		return GroupedOpenApi.builder()
							 .group("point")
							 .packagesToScan("com.explorer.gabom.domain.point") // point 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi questApi() {
		return GroupedOpenApi.builder()
							 .group("quest")
							 .packagesToScan("com.explorer.gabom.domain.quest") // quest 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi rankingApi() {
		return GroupedOpenApi.builder()
							 .group("ranking")
							 .packagesToScan("com.explorer.gabom.domain.ranking") // ranking 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi titleApi() {
		return GroupedOpenApi.builder()
							 .group("title")
							 .packagesToScan("com.explorer.gabom.domain.title") // title 관련 컨트롤러 있는 패키지
							 .build();
	}

	@Bean
	public GroupedOpenApi userApi() {
		return GroupedOpenApi.builder()
							 .group("user")
							 .packagesToScan("com.explorer.gabom.domain.user") // auth 관련 컨트롤러 있는 패키지
							 .build();
	}
}
