package com.explorer.gabom.global.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String RANKING_QUEUE = "ranking.exp.queue";

	// 큐 선언 (durable=true → 서버 재시작해도 큐 유지)
	@Bean
	public Queue rankingQueue() {
		return new Queue(RANKING_QUEUE, true);
	}

	// JSON 메시지 컨버터 등록
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// RabbitTemplate에 JSON 컨버터 적용
	@Bean
	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
}
