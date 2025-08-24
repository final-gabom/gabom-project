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

@Service
@RequiredArgsConstructor
public class PlaceSearchIndexer {

	private final ElasticsearchClient es;
	private final PlaceRepository placeRepo;
	private final AddressRepository addressRepo;
	private final PlaceSearchMapper mapper;

	private static final String IDX = "places_v1";

	@Transactional(readOnly = true)
	public void reindexAll() throws IOException {
		int page = 0, size = 500;
		while (true) {
			Page<Place> slice = placeRepo.findAll(PageRequest.of(page++, size));
			if (slice.isEmpty()) break;

			BulkRequest.Builder br = new BulkRequest.Builder();
			for (Place p : slice) {
				Address a = p.getAddressId() != null ? addressRepo.findById(p.getAddressId()).orElse(null) : null;
				PlaceSearchDoc doc = mapper.toDoc(p, a);
				br.operations(op -> op.index(i -> i.index(IDX).id(doc.getId()).document(doc.toMap())));
			}
			es.bulk(br.build());
		}
		es.indices().refresh(r -> r.index(IDX));
	}
}

