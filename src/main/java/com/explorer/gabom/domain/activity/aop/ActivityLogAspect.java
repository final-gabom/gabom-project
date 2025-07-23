package com.explorer.gabom.domain.activity.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.activity.repository.ActivityLogRepository;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

	private final ActivityLogRepository activityLogRepository;
	private final HttpServletRequest request;

	@AfterReturning(
		pointcut = "@annotation(com.explorer.gabom.domain.activity.aop.ActivityLoggable)",
		returning = "result"
	)
	public void logActivity(JoinPoint joinPoint, Object result) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		ActivityLoggable activityLoggable = signature.getMethod().getAnnotation(ActivityLoggable.class);
		ActivityType activityType = activityLoggable.value();

		Long userId = extractUserId();
		Long targetId = null;

		Object[] args = joinPoint.getArgs();

		if (activityType.getRequiredTargetId()) {
			targetId = extractTargetId(args);
			// if (targetId == null) {
			// 	targetId =
			// }
		}

		String ipAdress = request.getRemoteAddr();
	}

	private Long extractUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("인증된 사용자가 아닙니다.");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof User) {
			return ((User)principal).getId();
		}
		throw new IllegalStateException("알 수 없는 사용자입니다.");
	}

	private Long extractTargetId(Object[] args) {
		for (Object arg : args) {
			if (arg instanceof Long) {
				Long targetId = (Long)arg;
				return targetId;
			}
		}
		return null;
	}
}
