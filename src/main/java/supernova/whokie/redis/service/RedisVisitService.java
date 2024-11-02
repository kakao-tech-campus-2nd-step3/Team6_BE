package supernova.whokie.redis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.profile.service.ProfileVisitReadService;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.entity.RedisVisitor;
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;
import supernova.whokie.redis.infrastructure.repository.RedisVisitorRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        RedisVisitor redisVisitor = RedisVisitor.builder()
                .id(hostId + ":" + visitorIp)
                .hostId(hostId)
                .visitorIp(visitorIp)
                .visitTime(LocalDateTime.now())
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

    public List<RedisVisitCount> findAllVisitCount() {
        List<RedisVisitCount> visitCountList = new ArrayList<>();
        redisVisitCountRepository.findAll().forEach(visitCountList::add);
        return visitCountList;
    }

    public List<RedisVisitor> findAndDeleteAllVisitor() {
        List<RedisVisitor> visitorList = findAllVisitor();
        deleteAllVisitor();
        return visitorList;
    }

    public List<RedisVisitor> findAllVisitor() {
        List<RedisVisitor> visitorList = new ArrayList<>();
        redisVisitorRepository.findAll().forEach(visitorList::add);
        return visitorList;
    }

    public void deleteAllVisitor() {
        redisVisitorRepository.deleteAll();
    }
}
