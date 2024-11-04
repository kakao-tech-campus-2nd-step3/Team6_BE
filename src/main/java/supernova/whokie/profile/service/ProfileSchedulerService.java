package supernova.whokie.profile.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.ProfileVisitor;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.entity.RedisVisitor;
import supernova.whokie.redis.service.RedisVisitService;

import java.util.List;

@Profile("redis")
@Service
@AllArgsConstructor
public class ProfileSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(ProfileSchedulerService.class);

    private ProfileVisitCountWriterService profileVisitCountWriterService;
    private ProfileVisitorWriterService profileVisitorWriterService;
    private RedisVisitService redisVisitService;

    @Scheduled(cron = "0 0 * * * *")    // 매 정시(1시간 간격)마다 실행
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
    public void syncVisitorCountToDB() {
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

    private void logProcessedCount(int cnt) {
        log.info("Number of data processed: {}", cnt);
    }
}
