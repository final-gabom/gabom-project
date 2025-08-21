package com.explorer.gabom.domain.place.mapper;

import java.util.Map;
import java.util.Optional;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.entity.QAddress;
import com.explorer.gabom.domain.file.dto.ThumbnailDto;
import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;

/**
 * QueryDsl Mapping 전용 클래스
 */
public class PlaceSummaryMapper {
	private static final QPlace place = QPlace.place;
	private static final QAddress addr = QAddress.address;
	private static final QUser writer = QUser.user;
	private static final QTitle title = QTitle.title;
	private static final QAttachmentFile file = QAttachmentFile.attachmentFile;

	public static PlaceSummary fromTuple(Tuple t) {
		Long placeId = t.get(place.id);

		// 주소 DTO
		AddressDto addressDto = AddressDto.builder()
										  .id(t.get(addr.id))
										  .emdCd(t.get(addr.emdCd))
										  .detail(t.get(addr.detail))
										  .lat(t.get(addr.lat))
										  .lng(t.get(addr.lng))
										  .build();

		// 작성자 DTO
		UserSummaryDto writerDto = UserSummaryDto.builder()
												 .id(t.get(writer.id))
												 .nickname(t.get(writer.nickname))
												 .level(t.get(writer.level))
												 .title(t.get(title.name))
												 .build();

		// 썸네일 DTO
		ThumbnailDto thumbnail = null;
		String fileId = t.get(file.fileId);
		if (fileId != null) {
			thumbnail = ThumbnailDto.builder()
									.fileId(fileId)
									.filePath(t.get(file.filePath))
									.build();
		}

		return PlaceSummary.builder()
						   .placeId(placeId)
						   .title(t.get(place.title))
						   .missionProofCount(
							   Optional.ofNullable(t.get(Expressions.numberPath(Long.class, "proofCount")))
									   .map(Long::intValue).orElse(0)
						   )
						   .avgRating(null) // 별점 필요시 추가
						   .viewCount(Optional.ofNullable(t.get(place.viewCount)).orElse(0))
						   .writer(writerDto)
						   .thumbnail(thumbnail)
						   .distance(t.get(Expressions.numberPath(Double.class, "distance")))
						   .address(addressDto)
						   .build();
	}

	/**
	 * proofCountMap을 외부에서 주입받아 사용하는 버전
	 */
	public static PlaceSummary fromTuple(Tuple t, Map<Long, Integer> proofCountMap) {
		Long placeId = t.get(place.id);

		// 주소 DTO
		AddressDto addressDto = AddressDto.builder()
										  .id(t.get(addr.id))
										  .emdCd(t.get(addr.emdCd))
										  .detail(t.get(addr.detail))
										  .lat(t.get(addr.lat))
										  .lng(t.get(addr.lng))
										  .build();

		// 작성자 DTO
		UserSummaryDto writerDto = UserSummaryDto.builder()
												 .id(t.get(writer.id))
												 .nickname(t.get(writer.nickname))
												 .level(t.get(writer.level))
												 .title(t.get(title.name))
												 .build();

		// 썸네일 DTO
		ThumbnailDto thumbnail = null;
		String fileId = t.get(file.fileId);
		if (fileId != null) {
			thumbnail = ThumbnailDto.builder()
									.fileId(fileId)
									.filePath(t.get(file.filePath))
									.build();
		}

		return PlaceSummary.builder()
						   .placeId(placeId)
						   .title(t.get(place.title))
						   .missionProofCount(
							   Optional.ofNullable(proofCountMap.get(placeId)).orElse(0) // 🔹 별도 집계 결과 사용
						   )
						   .avgRating(null) // TODO: 별점 필요시 추가
						   .viewCount(Optional.ofNullable(t.get(place.viewCount)).orElse(0))
						   .writer(writerDto)
						   .thumbnail(thumbnail)
						   .distance(t.get(Expressions.numberPath(Double.class, "distance"))) // alias "distance"
						   .address(addressDto)
						   .build();
	}
}
