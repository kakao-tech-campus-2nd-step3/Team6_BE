package supernova.whokie.point_record.sevice.dto;

import lombok.Builder;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.controller.dto.PointRecordResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayReadyInfoResponse;

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
    @Builder
    public record ReadyInfo(
            String nextRedirectPcUrl
    ){
        public static PointRecordModel.ReadyInfo from(PayReadyInfoResponse payReadyInfoResponse){
            return ReadyInfo.builder().nextRedirectPcUrl(payReadyInfoResponse.nextRedirectPcUrl()).build();
        }

    }
}
