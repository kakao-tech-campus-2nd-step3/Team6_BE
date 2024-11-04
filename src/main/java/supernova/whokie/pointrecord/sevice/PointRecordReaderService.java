package supernova.whokie.pointrecord.sevice;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.infrastructure.repository.PointRecordRepository;
import supernova.whokie.pointrecord.sevice.dto.PointRecordCommand;
import supernova.whokie.pointrecord.sevice.dto.PointRecordModel;


@Service
@AllArgsConstructor
public class PointRecordReaderService {

    private final PointRecordRepository pointRecordRepository;

    @Transactional(readOnly = true)
    public Page<PointRecordModel.Record> getRecordsPaging(
        Long userId,
        PointRecordCommand.Record command,
        Pageable pageable
    ) {
        if (command.option() == PointRecordOption.ALL) {
            return pointRecordRepository.findByUserIdPaging(
                    userId, command.startDateTime(), command.endDate().atTime(LocalTime.MAX), pageable)
                .map(PointRecordModel.Record::from);
        }

        return pointRecordRepository.findByUserIdAndOptionPaging(
                userId, command.option(), command.startDateTime(), command.endDateTime(), pageable)
            .map(PointRecordModel.Record::from);
    }
}
