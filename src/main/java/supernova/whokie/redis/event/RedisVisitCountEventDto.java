package supernova.whokie.redis.event;

import lombok.Builder;

public class RedisVisitCountEventDto {

    @Builder
    public record Increment (
        Long hostId
    ) {

        public static RedisVisitCountEventDto.Increment toDto(Long hostId) {
            return Increment.builder()
                .hostId(hostId)
                .build();
        }
    }
}
