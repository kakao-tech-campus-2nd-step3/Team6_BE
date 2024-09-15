package supernova.whokie.answer.controller.dto;

import lombok.Builder;
import supernova.whokie.answer.Answer;
import supernova.whokie.user.controller.dto.UserResponse;

import java.time.LocalDate;
import java.util.List;

public class AnswerResponse {

    @Builder
    public record Refresh(
        List<UserResponse.PickedInfo> users
    ) {

    }

    @Builder
    public record Record(
            Long answerId,
            Long questionId,
            String questionContent,
            int hintCount,
            LocalDate createdAt
    ) {
        public static Record fromEntity(Answer answer){
            return new Record(
                    answer.getId(),
                    answer.getQuestion().getId(),
                    answer.getQuestion().getContent(),
                    answer.getHintCount(),
                    answer.getCreatedAt().toLocalDate()
                    );
        }
    }

    @Builder
    public record Hints(
            List<Hint> hints
    ) {

    }

    @Builder
    public record Hint(
            int hintNum,
            Boolean valid,
            String content
    ) {

    }
}
