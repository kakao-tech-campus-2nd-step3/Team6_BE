package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    @NotNull
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;
}
