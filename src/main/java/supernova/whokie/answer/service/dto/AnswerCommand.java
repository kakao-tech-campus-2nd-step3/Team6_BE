package supernova.whokie.answer.service.dto;

import lombok.Builder;

public class AnswerCommand {
    @Builder
    public record CommonAnswer(
            Long questionId,
            Long pickedId
    ) {
    }

    @Builder
    public record Purchase(
            Long answerId
    ) {

    }

    @Builder
    public record Group(
            Long questionId,
            Long groupId,
            Long pickedId
    ) {
    }
}
