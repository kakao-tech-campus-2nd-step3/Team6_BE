package supernova.whokie.global.url_provider_util;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UrlData(
    Long groupId,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime
) {

}
