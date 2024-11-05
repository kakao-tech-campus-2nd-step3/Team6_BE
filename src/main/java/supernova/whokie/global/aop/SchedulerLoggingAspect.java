package supernova.whokie.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SchedulerLoggingAspect {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object logScheduledMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();

        log.info("Scheduled task started: {}", methodName);
        log.info("Start time: {}", startTime);

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Error occurred while executing scheduler task: {}", e.getMessage(), e);
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Scheduled task finished: {}", methodName);
            log.info("End time: {}", endTime);
        }
    }
}
