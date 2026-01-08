package com.lbs.user.common.aop;

import com.lbs.user.common.annotation.TimeTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 작성자  : lbs
 * 날짜    : 2026-01-08
 * 풀이방법
 **/


@Slf4j
@Aspect
@Component
public class TimeTraceAspect {
    @Around("@annotation(timeTrace) || @within(timeTrace)")
    public Object traceTime(ProceedingJoinPoint joinPoint, TimeTrace timeTrace) throws Throwable {
            long start = System.currentTimeMillis();

            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();

            try {
                log.info("[TimeTrace] {}.{} 시작", className, methodName);
                Object result = joinPoint.proceed();
                return result;
            }finally {
                long endTime = System.currentTimeMillis();
                long excuTionTime = endTime - start;
                log.info("[TimeTrace] {}.{} 종료 - 실행시간 : {} ms", className, methodName, excuTionTime);

            }
    }
}
