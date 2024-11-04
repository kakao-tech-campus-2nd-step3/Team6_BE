package supernova.whokie.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SchedulerLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(SchedulerLoggingAspect.class);

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
