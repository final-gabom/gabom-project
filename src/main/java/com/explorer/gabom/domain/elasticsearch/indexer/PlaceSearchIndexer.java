package com.explorer.gabom.domain.elasticsearch.indexer;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.address.repository.AddressRepository;
import com.explorer.gabom.domain.elasticsearch.entity.PlaceSearchDoc;
import com.explorer.gabom.domain.elasticsearch.mapper.PlaceSearchMapper;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import lombok.RequiredArgsConstructor;

/**
 * Elasticsearch 색인(Indexing) 전용 서비스
 * - DB에 저장된 Place 엔티티 및 Address 엔티티를 읽어
 *   Elasticsearch "places_v1" 인덱스에 전송하는 역할.
 * - 주로 전체 리인덱싱(reindexAll)이나 배치성 업데이트 용도로 사용됨.
 */
@Service
@RequiredArgsConstructor
public class PlaceSearchIndexer {

	private final ElasticsearchClient es;       // Low-level ES Client (Java API)
	private final PlaceRepository placeRepo;    // Place 엔티티 조회용 JPA Repository
	private final AddressRepository addressRepo;// Address 엔티티 조회용 JPA Repository
	private final PlaceSearchMapper mapper;     // Place + Address → PlaceSearchDoc 변환기

	private static final String IDX = "places_v1"; // ES 인덱스명 (버전 관리 차원에서 v1 사용)

	/**
	 * 전체 Place 데이터를 Elasticsearch에 다시 색인(Reindex)한다.
	 * - 배치로 한 번에 너무 많은 데이터를 불러오지 않도록 페이징 처리한다.
	 * - Place -> Address -> PlaceSearchDoc 변환 후 Bulk API를 이용해 인덱싱한다.
	 *
	 * @throws IOException Elasticsearch 통신 중 발생할 수 있는 IO 예외
	 */
	@Transactional(readOnly = true)
	public void reindexAll() throws IOException {
		int page = 0, size = 500; // 페이징 단위 (500개씩)
		while (true) {
			// 1. DB에서 Place 엔티티 페이지 단위로 조회
			Page<Place> slice = placeRepo.findAll(PageRequest.of(page++, size));
			if (slice.isEmpty()) break; // 더 이상 데이터가 없으면 종료

			// 2. BulkRequest 빌더 생성
			BulkRequest.Builder br = new BulkRequest.Builder();

			// 3. 각 Place 엔티티 → Address 조회 → ES 문서 변환
			for (Place p : slice) {
				// Place에 연결된 Address 엔티티 조회 (nullable)
				Address a = p.getAddressId() != null
							? addressRepo.findById(p.getAddressId()).orElse(null)
							: null;

				// Mapper를 통해 Place + Address → PlaceSearchDoc 변환
				PlaceSearchDoc doc = mapper.toDoc(p, a);

				// ES Bulk 요청에 인덱스 작업 추가
				br.operations(op -> op.index(i ->
												 i.index(IDX)              // 대상 인덱스명
												  .id(doc.getId())        // 문서 ID (Place PK 등 고유값)
												  .document(doc.toMap())  // Map 형태로 직렬화한 Document
				));
			}

			// 4. Bulk API 실행 (일괄 색인)
			es.bulk(br.build());
		}

		// 5. 색인 강제 refresh → 즉시 검색 반영
		es.indices().refresh(r -> r.index(IDX));
	}
}
