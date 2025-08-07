package com.explorer.gabom.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
	@Schema(description = "사용자 닉네임", example = "gomgom", minLength = 2, maxLength = 10)
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하만 가능합니다.")
	private final String nickname;
	@Schema(description = "사용자 주소 코드", example = "1111010100")
	private final String addressCode;
	@Schema(description = "사용자 상세 주소", example = "와르르멘션 204호")
	private final String addressDetail;
	@Schema(description = "위도(latitude)", example = "37.123456")
	private final Double lat;
	@Schema(description = "경도(longitude)", example = "127.123456")
	private final Double lng;
	@Schema(description = "프로필 이미지 파일 ID", example = "file-123456")
	private final String profileImgId;
}