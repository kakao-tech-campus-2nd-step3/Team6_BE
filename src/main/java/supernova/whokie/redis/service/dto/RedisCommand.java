package supernova.whokie.redis.service.dto;

import lombok.Builder;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.redis.entity.RedisVisitCount;

public class RedisCommand {

    @Builder
    public record Visited(
            Long hostId,
            int dailyVisited,
            int totalVisited
    ) {
        public static RedisCommand.Visited from(ProfileVisitCount visitCount) {
            return RedisCommand.Visited.builder()
                    .hostId(visitCount.getHostId())
                    .dailyVisited(visitCount.getDailyVisited())
                    .totalVisited(visitCount.getTotalVisited())
                    .build();
        }

        public RedisVisitCount toRedisEntity() {
            return RedisVisitCount.builder()
                    .hostId(hostId)
                    .dailyVisited(dailyVisited)
                    .totalVisited(totalVisited)
                    .build();
        }
    }
}
