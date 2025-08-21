package com.explorer.gabom.domain.activity.aop;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.explorer.gabom.domain.activity.entity.AdminActivityLog;
import com.explorer.gabom.domain.activity.entity.UserActivityLog;
import com.explorer.gabom.domain.activity.repository.AdminActivityLogRepository;
import com.explorer.gabom.domain.activity.repository.UserActivityLogRepository;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.global.dto.TargetIdentifiable;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

	private final UserActivityLogRepository userActivityLogRepository;
	private final AdminActivityLogRepository adminActivityLogRepository;
	private final HttpServletRequest request;

	@AfterReturning(
		pointcut = "@annotation(com.explorer.gabom.domain.activity.aop.ActivityLoggable)",
		returning = "result"
	)
	public void logActivity(JoinPoint joinPoint, Object result) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		ActivityLoggable activityLoggable = signature.getMethod().getAnnotation(ActivityLoggable.class);
		ActivityType activityType = activityLoggable.value();

		CustomUserDetails currentUser = extractUser();
		Long userId = currentUser.getUserId();
		String userRole = currentUser.getRole();

		Long targetId = activityType.isRequiredTargetId() ?
						extractTargetId(signature, joinPoint.getArgs(), result) : null;

		String ipAddress = request.getRemoteAddr();
		String description = activityType.getMessage();

		if (userRole.equals("ROLE_ADMIN")) {
			AdminActivityLog adminActivityLog = new AdminActivityLog(userId, targetId, activityType, description,
																	 ipAddress);
			adminActivityLogRepository.save(adminActivityLog);
			log.info("관리자 활동 로그 저장: {}", adminActivityLog);
		} else {
			UserActivityLog userActivityLog = new UserActivityLog(userId, targetId, activityType, description,
																  ipAddress);
			userActivityLogRepository.save(userActivityLog);
			log.info("유저 활동 로그 저장: {}", userActivityLog);
		}
	}

	private CustomUserDetails extractUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("인증된 사용자가 아닙니다.");
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof CustomUserDetails customUserDetails) {
			return customUserDetails;
		}
		throw new IllegalStateException("알 수 없는 사용자입니다.");
	}

	private Long extractTargetId(MethodSignature signature, Object[] args, Object result) {
		Annotation[][] paramAnnotations = signature.getMethod().getParameterAnnotations();
		for (int i = 0; i < args.length; i++) {
			for (Annotation annotation : paramAnnotations[i]) {
				if (annotation instanceof TargetId && args[i] instanceof Long) {
					return (Long)args[i];
				}
			}
		}
		if (result instanceof TargetIdentifiable identifiable) {
			return identifiable.getTargetId();
		}

		return null;
	}
}
