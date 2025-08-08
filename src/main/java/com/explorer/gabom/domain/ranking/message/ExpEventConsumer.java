package com.explorer.gabom.domain.ranking.message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.explorer.gabom.global.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExpEventConsumer {

	private final StringRedisTemplate redisTemplate;

	@RabbitListener(queues = RabbitMQConfig.RANKING_QUEUE)
	public void consumeExpEvent(ExpEventMessage message) {
		String rankingKey = "ranking:exp";
		String userKey = "ranking:user:" + message.getUserId();

		// Redis ZSET: 랭킹 점수 저장
		redisTemplate.opsForZSet().add(rankingKey, String.valueOf(message.getUserId()), message.getExp());

		// Redis Hash: 유저 상세 정보 저장
		redisTemplate.opsForHash().put(userKey, "nickname", message.getNickname());
		redisTemplate.opsForHash().put(userKey, "titleName", message.getTitleName());
		redisTemplate.opsForHash().put(userKey, "profileImageId", message.getProfileImgId());
		redisTemplate.opsForHash().put(userKey, "level", String.valueOf(message.getLevel()));
	}
}
