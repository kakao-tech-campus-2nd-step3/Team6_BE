package supernova.whokie.point_record.event;

import lombok.Builder;
import supernova.whokie.point_record.PointRecordOption;

public class PointRecordEventDto {
    @Builder
    public record Earn(
            Long userId,
            int point,
            int amount,
            PointRecordOption option,
            String message
    ) {
        public static PointRecordEventDto.Earn toDto(Long userId, int point, int amount, PointRecordOption option, String message) {
            return Earn.builder()
                    .userId(userId)
                    .point(point)
                    .amount(amount)
                    .option(option)
                    .message(message).build();
        }
    }
}
