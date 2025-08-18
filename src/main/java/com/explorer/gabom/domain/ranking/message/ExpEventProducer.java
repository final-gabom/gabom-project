package com.explorer.gabom.domain.ranking.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.explorer.gabom.global.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExpEventProducer {

	private final RabbitTemplate rabbitTemplate;

	public void sendExpEvent(ExpEventMessage message) {
		rabbitTemplate.convertAndSend(RabbitMQConfig.RANKING_QUEUE, message);
	}
}
