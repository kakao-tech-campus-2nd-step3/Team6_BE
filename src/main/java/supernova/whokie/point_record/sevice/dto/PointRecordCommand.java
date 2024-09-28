package supernova.whokie.point_record.sevice.dto;

import lombok.Builder;
import supernova.whokie.point_record.PointRecordOption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PointRecordCommand {

    @Builder
    public record Record(
            LocalDate startDate,
            LocalDate endDate,
            PointRecordOption option
    ) {
        public LocalDateTime startDateTime() {
            return startDate.atStartOfDay();
        }

        public LocalDateTime endDateTime() {
            return endDate.atTime(LocalTime.MAX);
        }
    }
}
