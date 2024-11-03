package supernova.whokie.redis.entity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("payReady")
@Builder
@AllArgsConstructor
@Getter
public class PayToken {

    @Id
    private Long id;

    private String tid;

}
