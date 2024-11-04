package supernova.whokie.point_record.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.event.PointRecordEventDto;

@Service
@RequiredArgsConstructor
public class PointRecordService {

    private final PointRecordWriterService pointRecordWriterService;

    @Transactional
    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
                event.option(), event.message());
        pointRecordWriterService.save(pointRecord);
    }
}
