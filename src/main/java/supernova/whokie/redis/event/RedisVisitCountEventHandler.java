package supernova.whokie.redis.event;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import supernova.whokie.redis.service.RedisVisitService;

@Component
@AllArgsConstructor
public class RedisVisitCountEventHandler {

    private final RedisVisitService redisVisitService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void RedisVisitCountIncreaseListener(RedisVisitCountEventDto.Increment event) {
       redisVisitService.increaseVisitCount(event.hostId());
    }
}
