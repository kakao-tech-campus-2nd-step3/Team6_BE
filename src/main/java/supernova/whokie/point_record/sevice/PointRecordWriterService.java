package supernova.whokie.point_record.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.infrastructure.repository.PointRecordRepository;

@Service
@RequiredArgsConstructor
public class PointRecordWriterService {

    private final PointRecordRepository pointRecordRepository;

    @Transactional
    public void save(PointRecord pointRecord) {
        pointRecordRepository.save(pointRecord);
    }
}
