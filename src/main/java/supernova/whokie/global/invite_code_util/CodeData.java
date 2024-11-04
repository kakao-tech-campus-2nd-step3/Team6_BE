package supernova.whokie.global.invite_code_util;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CodeData(
    Long groupId,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime
) {

}
