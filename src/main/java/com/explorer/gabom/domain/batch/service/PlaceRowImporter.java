package com.explorer.gabom.domain.batch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.batch.dto.PlaceCsv;
import com.explorer.gabom.domain.batch.util.AddressRefResolver;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceRowImporter {

	// JPA 리포지토리 DI: 한 행(Place + Address) 저장에만 집중하는 전용 컴포넌트
	private final PlaceRepository placeRepository;
	private final AddressRepository addressRepository;
	private final AddressRefResolver addressRefResolver;

	/**
	 * CSV의 단일 행을 독립 트랜잭션(REQUIRES_NEW)으로 처리한다.
	 * - 장점: 이 행이 실패해도 바깥 배치는 계속 진행(부분 성공).
	 * - 트랜잭션 경계가 행마다 새로 열리므로, 이전 실패로 인한 rollback-only 전파를 막음.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void importOne(PlaceCsv row, User admin) {
		// 1) Place 생성/저장
		//    - CSV에서 들어온 필드들을 그대로 매핑
		//    - user는 “관리자 계정(고정)”으로 귀속
		//    - status는 APPROVED
		Place place = Place.builder()
						   .user(admin)
						   .title(row.getTitle())
						   .content(row.getContent())
						   .proofMethod(row.getProofMethod())
						   .viewCount(row.getViewCount() == null ? 0 : row.getViewCount())
						   .status(parseStatus(row.getStatus()))
						   .build();
		place = placeRepository.save(place);  // ID 확정 (address.targetId로 사용 예정)

		// 2) 주소 참조 해석
		var emd = addressRefResolver.byEmdCd(row.getEmdCd());

		// 3) Address 생성/저장
		//    - addressTypeCd: enum 매핑을 위한 실제 코드값(문자열) 컬럼. enum 필드는 읽기전용일 수 있어 코드컬럼에 직접 세팅
		//    - targetId: 이 주소가 귀속될 주체의 PK(여기서는 place.id)
		//    - emdCd: FK로 쓰이는 실제 컬럼(문자열 코드). 연관관계(eupmyeondong)는 조회용
		//    - detail/lat/lng: CSV의 원문 주소와 위경도
		Address addr = Address.builder()
							  .addressTypeCd("PLACE")
							  .targetId(place.getId())
							  .eupmyeondong(emd)          // 조회/연관용(실제 FK 컬럼은 emdCd)
							  .emdCd(emd.getEmdCd())      // 실제 FK 값
							  .detail(row.getAddress())   // 사용자에게 보이는 원문 주소 문자열
							  .lat(row.getLat())
							  .lng(row.getLng())
							  .build();
		addr = addressRepository.save(addr);  // ID 확정 (place.addressId로 연결)

		// 4) Place ↔ Address 연결
		//    - 엔티티에 정의된 의미 메서드(linkAddress)를 통해 양방향(or 단방향) 연결 값 세팅
		//    - 내부에서 place.address = addr, place.addressId = addr.getId() 같이 PK 기반 FK 세팅을 수행
		place.linkAddress(addr);

		// 5) 업데이트 반영
		//    - link 이후 place의 FK(address_id)가 갱신되므로 다시 save
		placeRepository.save(place);
	}

	/**
	 * 초기데이터 APPROVED 로 저장
	 */
	private PlaceStatus parseStatus(String raw) {
		return PlaceStatus.APPROVED;
	}
}


