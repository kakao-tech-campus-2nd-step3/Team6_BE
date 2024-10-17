package supernova.whokie.redis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.service.ProfileVisitReadService;
import supernova.whokie.redis.entity.VisitCount;
import supernova.whokie.redis.entity.Visitor;
import supernova.whokie.redis.repository.VisitCountRepository;
import supernova.whokie.redis.repository.VisitorRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class RedisVisitService {

    private VisitorRepository visitorRepository;
    private VisitCountRepository visitCountRepository;
    private ProfileVisitReadService profileVisitReadService;

    public VisitCount visitProfile(Long hostId, String visitorIp) {
        VisitCount visitCount = findVisitCountByHostId(hostId);
        if(!checkVisited(hostId, visitorIp)) {
            visitCount.visit();
            visitCountRepository.save(visitCount);
        }
        // 방문자 로그 기록
        saveVisitor(hostId, visitorIp);

        return visitCount;
    }

    public boolean checkVisited(Long hostId, String visitorIp) {
        return visitorRepository.existsById(hostId + ":" + visitorIp);
    }

    public void saveVisitor(Long hostId, String visitorIp) {
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999); // 오늘 23:59:59.999999
        LocalDateTime now = LocalDateTime.now();
        long expiresIn = ChronoUnit.SECONDS.between(now, endOfDay);

        Visitor visitor = Visitor.builder()
                .id(hostId + ":" + visitorIp)
                .hostId(hostId)
                .visitorIp(visitorIp)
                .visitTime(now)
                .expiresIn(expiresIn)
                .build();
        visitorRepository.save(visitor);
    }

    public VisitCount findVisitCountByHostId(Long hostId) {
        return visitCountRepository.findById(hostId)
                .orElseGet(() -> {
                    RedisCommand.Visited command = profileVisitReadService.findVisitCountById(hostId);
                    return command.toRedisEntity();
                });
    }
}
