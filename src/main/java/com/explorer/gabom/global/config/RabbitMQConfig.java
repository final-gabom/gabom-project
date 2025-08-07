package com.explorer.gabom.global.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String RANKING_QUEUE = "ranking.exp.queue";

	@Bean
	public Queue rankingQueue() {
		return new Queue(RANKING_QUEUE, true);
	}
}
