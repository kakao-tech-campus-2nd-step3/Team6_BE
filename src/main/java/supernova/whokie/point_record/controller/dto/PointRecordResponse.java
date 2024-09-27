package supernova.whokie.point_record.controller.dto;

import lombok.Builder;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.sevice.dto.PointRecordModel;

import java.time.LocalDate;

public class PointRecordResponse {

    @Builder
    public record Record(
            Long id,
            int point,
            PointRecordOption option,
            LocalDate createdAt
    ) {
        public static PointRecordResponse.Record from(PointRecordModel.Record model) {
            return PointRecordResponse.Record.builder()
                    .id(model.id())
                    .point(model.point())
                    .option(model.option())
                    .createdAt(model.createdAt())
                    .build();
        }
    }
}
