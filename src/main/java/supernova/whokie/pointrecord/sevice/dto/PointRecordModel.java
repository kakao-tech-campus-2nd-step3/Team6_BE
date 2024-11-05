package supernova.whokie.pointrecord.sevice.dto;

import lombok.Builder;
import supernova.whokie.pointrecord.PointRecord;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.infrastructure.apicaller.dto.PayReadyInfoResponse;

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
