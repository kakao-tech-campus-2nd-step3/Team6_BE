package supernova.whokie.profile_answer.controller.dto;

import lombok.Builder;

public class ProfileAnswerRequest {

    @Builder
    public record Answer(
            String content,
            Long profileQuestionId
    ) {

    }
}
