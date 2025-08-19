package com.explorer.gabom.global.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.exploration.entity.Exploration;
import com.explorer.gabom.domain.exploration.repository.ExplorationRepository;
import com.explorer.gabom.domain.notification.service.NotificationService;
import com.explorer.gabom.domain.notification.type.NotificationRefType;
import com.explorer.gabom.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExplorationAlarmScheduler {

	private static final Logger log = LoggerFactory.getLogger(ExplorationAlarmScheduler.class);
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	private final NotificationService notificationService;
	private final ExplorationRepository explorationRepository;
	private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
	//  운영 기본: 30분 전
	@Value("${spring.exploration.alarm.almost-minutes:30}")
	private long almostMinutes;
	// 로컬 테스트용: 초 단위 임박 (설정되면 minutes 대신 이 값 우선)
	@Value("${spring.exploration.alarm.almost-seconds:0}")
	private long almostSeconds;

	private String key(Long id, String kind) {
		return id + ":" + kind;
	}

	public void schedule(Exploration e) {
		LocalDateTime endAt = e.getEndAt();
		LocalDateTime almostAt = (almostSeconds > 0)
								 ? endAt.minusSeconds(almostSeconds)
								 : endAt.minusMinutes(almostMinutes);

		long almostDelay = millisUntil(almostAt);
		long expireDelay = millisUntil(endAt);

		// 임박 알림
		if (almostDelay > 0 && e.isActive() && !e.isAlmostNotified()) {
			var f = scheduler.schedule(() -> {
				explorationRepository.findById(e.getId()).ifPresent(cur -> {
					if (cur.isActive() && !cur.isAlmostNotified()) {
						String remain = (almostSeconds > 0) ? (almostSeconds + "초") : (almostMinutes + "분");
						notificationService.notify(
							cur.getUser().getId(),
							NotificationType.QUEST_TIME_RUNNING_OUT,
							"퀘스트 제한 시간이" + remain + "남았습니다! 서둘러 인증해주세요.",
							"/explorations/" + cur.getId(),
							NotificationRefType.EXPLORATION,
							cur.getId()
						);
						cur.markAlmostNotified();
						explorationRepository.save(cur);
						log.info("[ALMOST] pushed user={}, exp={}, at={}", cur.getUser().getId(), cur.getId(), LocalDateTime.now());
					}
				});
			}, almostDelay, TimeUnit.MILLISECONDS);
			futures.put(key(e.getId(), "almost"), f);
			log.debug("Scheduled ALMOST for exploration={} at={}", e.getId(), almostAt);
		}

		//만료 알림 (중복 방지)
		if (expireDelay > 0 && !e.isExpiredNotified()) {
			var f = scheduler.schedule(() -> {
				explorationRepository.findById(e.getId()).ifPresent(cur -> {
					if (!cur.isExpiredNotified()) {
						if (cur.isActive()) {
							cur.markExpired();
						}
						cur.markExpiredNotified();
						explorationRepository.save(cur);

						notificationService.notify(
							cur.getUser().getId(),
							NotificationType.QUEST_EXPIRED,
							"퀘스트 시간이 만료되었습니다.",
							"/explorations/" + cur.getId(),
							NotificationRefType.EXPLORATION,
							cur.getId()
						);
					}
				});
			}, expireDelay, TimeUnit.MILLISECONDS);
			futures.put(key(e.getId(), "expire"), f);
			log.debug("Scheduled EXPIRE for exploration={} at={}", e.getId(), endAt);
		}
	}

	public void cancel(Long explorationId) {
		var a = futures.remove(key(explorationId, "almost"));
		if (a != null)
			a.cancel(false);
		var b = futures.remove(key(explorationId, "expire"));
		if (b != null)
			b.cancel(false);
	}

	// 연장 시 재등록
	public void reschedule(Exploration e) {
		cancel(e.getId());
		schedule(e);
	}

	// 앱 시작 시 복구
	public void bootstrapPending() {
		explorationRepository.findAllByStatusAndEndAtAfter(
			Exploration.Status.IN_PROGRESS, LocalDateTime.now()
		).forEach(this::schedule);
	}

	private long millisUntil(LocalDateTime t) {
		return Math.max(0, java.time.Duration.between(LocalDateTime.now(), t).toMillis());
	}
}
