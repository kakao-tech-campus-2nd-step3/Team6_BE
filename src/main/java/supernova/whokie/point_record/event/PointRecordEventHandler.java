package supernova.whokie.point_record.event;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import supernova.whokie.point_record.sevice.PointRecordService;

@Component
@AllArgsConstructor
public class PointRecordEventHandler {

    private final PointRecordService pointRecordService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void earnPointListener(PointRecordEventDto.Earn event) {

        pointRecordService.recordEarnPoint(event);

    }
}
