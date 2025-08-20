package com.explorer.gabom.domain.batch.service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.address.repository.EupmyeondongRepository;
import com.explorer.gabom.domain.batch.dto.PlaceCsv;
import com.explorer.gabom.domain.batch.util.AddressCodeUtils;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceRowImporter {

	// JPA 리포지토리 DI: 한 행(Place + Address) 저장에만 집중하는 전용 컴포넌트
	private final PlaceRepository placeRepository;
	private final AddressRepository addressRepository;
	private final EupmyeondongRepository eupmyeondongRepository;

	/**
	 * 처음 한 번만 DB에서 모든 emdCd를 읽어 Set으로 보관.
	 * (서울 CSV면 중복 emdCd가 매우 많으므로 exists 쿼리 제거 효과 큼)
	 */
	private final AtomicReference<Set<String>> emdCdAllCache = new AtomicReference<>();

	private Set<String> getEmdSet() {
		Set<String> cached = emdCdAllCache.get();
		if (cached != null) return cached;
		synchronized (emdCdAllCache) {
			cached = emdCdAllCache.get();
			if (cached == null) {
				cached = eupmyeondongRepository.findAllEmdCd().stream().collect(Collectors.toUnmodifiableSet());
				emdCdAllCache.set(cached);
				log.info("[PlaceRowImporter] emdCd preload completed: {} codes", cached.size());
			}
		}
		return cached;
	}


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

		String emd = row.getEmdCd();
		validateEmdCd(emd);

		// 3) Address 생성/저장
		//    - addressTypeCd: enum 매핑을 위한 실제 코드값(문자열) 컬럼. enum 필드는 읽기전용일 수 있어 코드컬럼에 직접 세팅
		//    - targetId: 이 주소가 귀속될 주체의 PK(여기서는 place.id)
		//    - emdCd: FK로 쓰이는 실제 컬럼(문자열 코드). 연관관계(eupmyeondong)는 조회용
		//    - detail/lat/lng: CSV의 원문 주소와 위경도
		Address addr = Address.builder()
							  .addressTypeCd("PLACE")
							  .targetId(place.getId())
							  .sdCd(AddressCodeUtils.sdCd(emd))
							  .sggCd(AddressCodeUtils.sggCd(emd))
							  .emdCd(emd)                  // 실제 FK 값
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

	// EmdCd 형식(10자리 문자), 존재 여부 검증
	private void validateEmdCd(String emdCd) {
		if (!AddressCodeUtils.isValidLawCode(emdCd)) {
			log.warn("잘못된 EMD 코드 형식: {}", emdCd);
			throw new IllegalArgumentException("잘못된 endCd 형식: " + emdCd);
		}
		if (!getEmdSet().contains(emdCd)) {
			log.warn("존재하지 않는 EMD 코드 감지: {}", emdCd);
			throw new CustomException(ErrorCode.EMD_CODE_NOT_FOUND);
		}
	}

	/**
	 * 초기데이터 APPROVED 로 저장
	 */
	private PlaceStatus parseStatus(String raw) {
		return PlaceStatus.APPROVED;
	}
}


