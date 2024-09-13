package supernova.whokie.point_record.controller.dto;

import lombok.Builder;
import supernova.whokie.point_record.PointRecordOption;

import java.time.LocalDate;

public class PointRecordResponse {

    @Builder
    public record Record(
            Long id,
            int point,
            PointRecordOption option,
            LocalDate createAt
    ) {

    }
}
