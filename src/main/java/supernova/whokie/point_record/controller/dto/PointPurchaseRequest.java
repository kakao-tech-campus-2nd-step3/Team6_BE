package supernova.whokie.point_record.controller.dto;

import lombok.Builder;

public class PointPurchaseRequest {

    @Builder
    public record Purchase(
            int amount
    ) {

    }
}
