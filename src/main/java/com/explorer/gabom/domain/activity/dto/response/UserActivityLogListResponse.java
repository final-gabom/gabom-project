package com.explorer.gabom.domain.activity.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLogListResponse {
	private List<UserActivityLogResponse> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public static UserActivityLogListResponse toDto(Page<UserActivityLogResponse> page) {
		return new UserActivityLogListResponse(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}
}