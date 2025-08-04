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
			.title("데모 프로젝트 API Document")
			.version("v0.0.1")
			.description("데모 프로젝트의 API 명세서입니다.");

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
}
