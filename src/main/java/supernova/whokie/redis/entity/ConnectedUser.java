package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import reactor.core.publisher.FluxSink;

@Builder
@RedisHash("ConnectedUser")
public class ConnectedUser {

    @Id
    private Long userId;

    @NotNull
    private FluxSink<String> sink;

    public FluxSink<String> getSink() {
        return sink;
    }
}
