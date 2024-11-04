package supernova.whokie.pointrecord.controller.dto;

import lombok.Builder;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.sevice.dto.PointRecordModel;

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
