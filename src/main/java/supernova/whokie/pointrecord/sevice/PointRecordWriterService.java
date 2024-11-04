package supernova.whokie.pointrecord.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.pointrecord.PointRecord;
import supernova.whokie.pointrecord.infrastructure.repository.PointRecordRepository;

@Service
@RequiredArgsConstructor
public class PointRecordWriterService {

    private final PointRecordRepository pointRecordRepository;

    @Transactional
    public void save(PointRecord pointRecord) {
        pointRecordRepository.save(pointRecord);
    }
}
