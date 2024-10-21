package supernova.whokie.redis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.service.ProfileVisitReadService;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.entity.RedisVisitor;
import supernova.whokie.redis.repository.RedisVisitCountRepository;
import supernova.whokie.redis.repository.RedisVisitorRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class RedisVisitService {

    private RedisVisitorRepository redisVisitorRepository;
    private RedisVisitCountRepository redisVisitCountRepository;
    private ProfileVisitReadService profileVisitReadService;

    public RedisVisitCount visitProfile(Long hostId, String visitorIp) {
        RedisVisitCount redisVisitCount = findVisitCountByHostId(hostId);
        if(!checkVisited(hostId, visitorIp)) {
            redisVisitCount.visit();
            redisVisitCountRepository.save(redisVisitCount);
        }
        // 방문자 로그 기록
        saveVisitor(hostId, visitorIp);

        return redisVisitCount;
    }

    public boolean checkVisited(Long hostId, String visitorIp) {
        return redisVisitorRepository.existsById(hostId + ":" + visitorIp);
    }

    public void saveVisitor(Long hostId, String visitorIp) {
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999); // 오늘 23:59:59.999999
        LocalDateTime now = LocalDateTime.now();
        long expiresIn = ChronoUnit.SECONDS.between(now, endOfDay);

        RedisVisitor redisVisitor = RedisVisitor.builder()
                .id(hostId + ":" + visitorIp)
                .hostId(hostId)
                .visitorIp(visitorIp)
                .visitTime(now)
                .expiresIn(expiresIn)
                .build();
        redisVisitorRepository.save(redisVisitor);
    }

    public RedisVisitCount findVisitCountByHostId(Long hostId) {
        return redisVisitCountRepository.findById(hostId)
                .orElseGet(() -> {
                    RedisCommand.Visited command = profileVisitReadService.findVisitCountById(hostId);
                    return command.toRedisEntity();
                });
    }
}
