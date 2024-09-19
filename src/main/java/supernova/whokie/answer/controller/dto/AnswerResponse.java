package supernova.whokie.answer.controller.dto;

import lombok.Builder;
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
        public static Record from(AnswerRecord answerRecord){
            return new Record(
                    answerRecord.getAnswerId(),
                    answerRecord.getQuestionId(),
                    answerRecord.getQuestionContent(),
                    answerRecord.getHintCount(),
                    answerRecord.getCreatedAt()
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
