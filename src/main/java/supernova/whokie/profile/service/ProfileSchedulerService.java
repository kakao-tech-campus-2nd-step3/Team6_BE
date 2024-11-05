package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.ProfileVisitor;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.entity.RedisVisitor;
import supernova.whokie.redis.service.RedisVisitService;

import java.util.List;

@Slf4j
@Profile("redis")
@Service
@RequiredArgsConstructor
public class ProfileSchedulerService {

    private final ProfileVisitCountWriterService profileVisitCountWriterService;
    private final ProfileVisitorWriterService profileVisitorWriterService;
    private final RedisVisitService redisVisitService;

    @Scheduled(cron = "0 0 * * * *")    // 매 정시(1시간 간격)마다 실행
    @Transactional
    public void syncVisitCountToDB() {
        List<RedisVisitCount> redisEntities = redisVisitService.findAllVisitCounts();
        List<ProfileVisitCount> dbEntities = redisEntities.stream()
                .map(redis -> ProfileVisitCount.builder()
                        .hostId(redis.getHostId())
                        .dailyVisited(redis.getDailyVisited())
                        .totalVisited(redis.getTotalVisited())
                        .build())
                .toList();
        profileVisitCountWriterService.saveAll(dbEntities);

        logProcessedCount(dbEntities.size());
    }

    @Scheduled(cron = "0 0 0 * * *")    // 매 자정(0시)마다 실행
    @Transactional
    public void syncVisitorToDB() {
        List<RedisVisitor> redisEntities = redisVisitService.findAllVisitors();
        List<ProfileVisitor> dbEntities = redisEntities.stream()
                .map(redis -> ProfileVisitor.builder()
                        .visitorIp(redis.getVisitorIp())
                        .hostId(redis.getHostId())
                        .visitTime(redis.getVisitTime())
                        .build())
                .toList();
        profileVisitorWriterService.saveAll(dbEntities);
        redisVisitService.deleteAllVisitors(redisEntities);

        logProcessedCount(dbEntities.size());
    }

    @Scheduled(cron = "0 0 0 * * *")    // 매 자정(0시)마다 실행
    public void updateVisitCount() {
        List<RedisVisitCount> redisEntities = redisVisitService.findAllVisitCounts();
        redisVisitService.updateAllVisitCounts(redisEntities);

        logProcessedCount(redisEntities.size());
    }

    private void logProcessedCount(int cnt) {
        log.info("Number of data processed: {}", cnt);
    }
}
