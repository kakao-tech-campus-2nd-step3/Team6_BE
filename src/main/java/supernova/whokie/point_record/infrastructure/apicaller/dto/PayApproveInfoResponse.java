package supernova.whokie.point_record.infrastructure.apicaller.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PayApproveInfoResponse(
        String aid,
        String tid,
        String cid,
        String partnerOrderId,
        String partnerUserId,
        String itemName,
        int quantity,
        Amount amount,
        String paymentMethodType,
        String createdAt,
        String approvedAt
) {
    public record Amount(
            int total,
            int taxFree,
            int vat,
            int point,
            int discount,
            int greenDeposit
    ) {}
}
