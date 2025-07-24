package com.explorer.gabom.domain.title.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCreateResponse {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;



}
