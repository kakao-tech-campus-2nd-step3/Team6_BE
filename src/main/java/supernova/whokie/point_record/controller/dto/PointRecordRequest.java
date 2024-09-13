package supernova.whokie.point_record.controller.dto;

import lombok.Builder;

public class PointRecordRequest {

    @Builder
    public record Purchase(
            int point
    ) {

    }

    @Builder
    public record Earn(
            int point
    ) {

    }
}
