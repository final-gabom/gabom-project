package com.explorer.gabom.global.config;

import java.net.URI;

import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

	@Bean
	public RestClient restClient(@Value("${elasticsearch.url}") String url) {
		// url: http://localhost:9200  (반드시 http:// 포함, 공백/따옴표 없이)
		URI uri = URI.create(url.trim());
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = (uri.getPort() == -1)
				   ? ("https".equalsIgnoreCase(scheme) ? 443 : 80)
				   : uri.getPort();

		return RestClient.builder(new org.apache.http.HttpHost(host, port, scheme)).build();
	}

	@Bean
	public ElasticsearchTransport transport(RestClient restClient) {
		return new RestClientTransport(restClient, new JacksonJsonpMapper());
	}

	@Bean
	public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
		return new ElasticsearchClient(transport);
	}
}
