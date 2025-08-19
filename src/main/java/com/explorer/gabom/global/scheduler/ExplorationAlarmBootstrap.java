package com.explorer.gabom.global.scheduler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExplorationAlarmBootstrap implements ApplicationRunner{

	private final ExplorationAlarmScheduler scheduler;

	@Override
	public void run(ApplicationArguments args) {
		scheduler.bootstrapPending();
	}
}
