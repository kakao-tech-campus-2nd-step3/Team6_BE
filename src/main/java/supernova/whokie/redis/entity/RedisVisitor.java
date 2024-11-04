package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash("Visitor")
@Builder
public class RedisVisitor {

    @Id
    private String id;

    private Long hostId;

    @NotNull
    private String visitorIp;

    @NotNull
    private LocalDateTime visitTime;

    public String getId() {
        return id;
    }

    public Long getHostId() {
        return hostId;
    }

    public @NotNull String getVisitorIp() {
        return visitorIp;
    }

    public @NotNull LocalDateTime getVisitTime() {
        return visitTime;
    }
}
