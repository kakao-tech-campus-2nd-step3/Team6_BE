package supernova.whokie.global.dto;

import lombok.Builder;

@Builder
public record GlobalResponse(
        String message
) {

}
