package supernova.whokie.point_record.sevice.dto;

import lombok.Builder;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;

import java.time.LocalDate;

public class PointRecordModel {

    @Builder
    public record Record(
        Long id,
        int point,
        PointRecordOption option,
        LocalDate createdAt
    ) {
        public static PointRecordModel.Record from(PointRecord entity) {
            return PointRecordModel.Record.builder()
                    .id(entity.getId())
                    .point(entity.getPoint())
                    .option(entity.getOption())
                    .createdAt(LocalDate.from(entity.getCreatedAt()))
                    .build();
        }
    }
}
