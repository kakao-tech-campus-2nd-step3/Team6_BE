package supernova.whokie.point.controller.dto;

import lombok.Builder;

public class PointRequest {

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
