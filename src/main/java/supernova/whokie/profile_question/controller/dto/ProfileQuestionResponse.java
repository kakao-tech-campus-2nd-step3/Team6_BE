package supernova.whokie.profile_question.controller.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ProfileQuestionResponse {

    @Builder
    public record Questions(
            List<Question> questions
    ) {

    }

    @Builder
    public record Question(
            Long profileQuestionId,
            String profileQuestionContent,
            LocalDate createdAt
    ) {

    }
}
