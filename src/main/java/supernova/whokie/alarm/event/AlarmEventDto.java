package supernova.whokie.alarm.event;

import lombok.Builder;

public class AlarmEventDto {
    @Builder
    public record Alarm(
            Long userId,
            String question
    ) {
        public static AlarmEventDto.Alarm toDto(Long userId, String question) {
            return Alarm.builder()
                    .userId(userId)
                    .question(question)
                    .build();
        }
    }
}
