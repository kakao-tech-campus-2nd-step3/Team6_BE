package supernova.whokie.user.infrastructure.apiCaller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenInfoResponse(
        String accessToken,
        String tokenType,
        String refreshToken,
        Long expiresIn,
        Long refreshTokenExpiresIn
) {

}
