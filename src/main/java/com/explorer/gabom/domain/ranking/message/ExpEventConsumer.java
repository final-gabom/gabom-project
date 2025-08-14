package com.explorer.gabom.domain.ranking.message;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.ranking.entity.Ranking;
import com.explorer.gabom.domain.ranking.repository.RankingRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ExpEventConsumer {

	private final RankingRepository rankingRepository;
	private final UserRepository userRepository;

	@RabbitListener(queues = RabbitMQConfig.RANKING_QUEUE)
	@Transactional
	public void consumeExpEvent(ExpEventMessage message) {
		User user = userRepository.getReferenceById(message.getUserId());

		Optional<Ranking> existing = rankingRepository.findByUser_Id(message.getUserId());
		if (existing.isPresent()) {
			Ranking ranking = existing.get();
			ranking.updateExp(message.getExp());
			rankingRepository.save(ranking);
		} else {
			rankingRepository.save(new Ranking(user, message.getExp()));
		}
	}
}
