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

	private int page = 0;
	private int size = 10;
	private String sort = "createdAt,desc";  // 정렬 기준
}
