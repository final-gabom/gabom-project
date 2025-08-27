package com.explorer.gabom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.explorer.gabom.domain.elasticsearch.indexer.PlaceSearchIndexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@SpringBootTest
@ActiveProfiles("test")
class GabomApplicationTests {

    @MockBean
    ElasticsearchClient elasticsearchClient;

    @MockBean
    PlaceSearchIndexer placeSearchIndexer;

    @Test
    void contextLoads() {
    }

}
