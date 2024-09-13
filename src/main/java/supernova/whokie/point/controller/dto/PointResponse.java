package supernova.whokie.point.controller.dto;

import lombok.Builder;
import supernova.whokie.point.PointRecordOption;

import java.time.LocalDate;

public class PointResponse {

    @Builder
    public record Record(
            Long id,
            int point,
            PointRecordOption option,
            LocalDate createAt
    ) {

    }
}
