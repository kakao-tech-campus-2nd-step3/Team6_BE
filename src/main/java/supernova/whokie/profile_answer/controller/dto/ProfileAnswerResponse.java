package supernova.whokie.profile_answer.controller.dto;

import lombok.Builder;

import java.time.LocalDate;

public class ProfileAnswerResponse {

    @Builder
    public record Answer(
            Long profileAnswerId,
            String content,
            String profileQuestionContent,
            LocalDate createdAt
    ) {

    }
}
