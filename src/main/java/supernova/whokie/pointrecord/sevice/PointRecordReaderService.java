package supernova.whokie.pointrecord.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.pointrecord.PointRecord;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.infrastructure.repository.PointRecordRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PointRecordReaderService {

    private final PointRecordRepository pointRecordRepository;

    @Transactional(readOnly = true)
    public Page<PointRecord> getRecordsByUserId(
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Pageable pageable
    ) {
        return pointRecordRepository.findByUserIdPaging(
                userId, startTime, endTime, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PointRecord> getRecordsByUserIdAndOption(
            Long userId,
            PointRecordOption option,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    ) {
        return pointRecordRepository.findByUserIdAndOptionPaging(
                        userId, option, startTime, endTime, pageable);
    }
}
