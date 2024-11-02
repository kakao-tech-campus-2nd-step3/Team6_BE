package supernova.whokie.profile.service;

import lombok.AllArgsConstructor;
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
    private ProfileVisitCountWriterService profileVisitCountWriterService;
    private ProfileVisitorWriterService profileVisitorWriterService;
    private RedisVisitService redisVisitService;

    @Scheduled(cron = "0 0 * * * *")    // 매 정시(1시간 간격)마다 실행
    public void syncVisitCountToDB() {
        List<RedisVisitCount> redisEntities = redisVisitService.findAllVisitCount();
        List<ProfileVisitCount> dbEntities = redisEntities.stream()
                .map(redis -> ProfileVisitCount.builder()
                        .hostId(redis.getHostId())
                        .dailyVisited(redis.getDailyVisited())
                        .totalVisited(redis.getTotalVisited())
                        .build())
                .toList();
        profileVisitCountWriterService.saveAll(dbEntities);
    }

    @Scheduled(cron = "0 0 0 * * *")    // 매 자정(0시)마다 실행
    public void syncVisitorCountToDB() {
        List<RedisVisitor> redisEntities = redisVisitService.findAndDeleteAllVisitor();
        List<ProfileVisitor> dbEntities = redisEntities.stream()
                .map(redis -> ProfileVisitor.builder()
                        .visitorIp(redis.getVisitorIp())
                        .hostId(redis.getHostId())
                        .visitTime(redis.getVisitTime())
                        .build())
                .toList();
        profileVisitorWriterService.saveAll(dbEntities);
    }
}
