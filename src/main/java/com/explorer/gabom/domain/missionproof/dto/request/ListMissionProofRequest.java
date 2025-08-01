package com.explorer.gabom.domain.missionproof.dto.request;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListMissionProofRequest {
	private MissionProofType type;         // ex. "PLACE", "EVENT"
	private Long targetId;       // 장소 또는 이벤트 ID
	private Long userId;         // 인증 작성자 ID
	private Long lastId;         // 커서 기반 페이징 (이전 마지막 ID)
	private Integer size = 10;       // 한 페이지 크기
	private String sort = "createdAt,desc";  // 정렬 기준
}
