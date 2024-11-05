package supernova.whokie.pointrecord.infrastructure.apicaller.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PayReadyInfoResponse(
        String tid,
        String nextRedirectPcUrl
) {
}
