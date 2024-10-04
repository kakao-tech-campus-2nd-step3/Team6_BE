package supernova.whokie.answer.controller.dto;

import jakarta.validation.constraints.NotNull;
import supernova.whokie.answer.service.dto.AnswerCommand;

public class AnswerRequest {

    public record Common(
            @NotNull
            Long questionId,
            @NotNull
            Long pickedId
    ) {
        public AnswerCommand.CommonAnswer toCommand() {
            return AnswerCommand.CommonAnswer.builder()
                    .questionId(this.questionId)
                    .pickedId(this.pickedId)
                    .build();
        }

    }

    public record Group(
            Long questionId,
            Long groupId,
            Long pickedId
    ) {

    }

    public record Purchase(
            @NotNull
            Long answerId
    ) {
        public AnswerCommand.Purchase toCommand() {
            return AnswerCommand.Purchase.builder()
                    .answerId(this.answerId)
                    .build();
        }
    }
}
