package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("VisitCount")
@Builder
public class VisitCount {
    @Id
    private Long hostId;

    @NotNull
    private int dailyVisited;

    @NotNull
    private int totalVisited;

    public void visit() {
        dailyVisited += 1;
        totalVisited += 1;
    }

    public Long getHostId() {
        return hostId;
    }

    public int getDailyVisited() {
        return dailyVisited;
    }

    public int getTotalVisited() {
        return totalVisited;
    }
}
