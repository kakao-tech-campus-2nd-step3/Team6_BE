package supernova.whokie.point_record.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class PointRecordRequest {

    @Builder
    public record Purchase(
            @NotNull @Min(0)
            int point
    ) {

    }

    @Builder
    public record Earn(
            @NotNull @Min(0)
            int point
    ) {

    }
}
