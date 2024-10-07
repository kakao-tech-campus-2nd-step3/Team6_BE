package supernova.whokie.answer.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import supernova.whokie.answer.service.dto.AnswerCommand;

public class AnswerRequest {

    public record Common(
            @NotNull @Min(1)
            Long questionId,
            @NotNull @Min(1)
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
            @NotNull @Min(1)
            Long questionId,
            @NotNull @Min(1)
            Long groupId,
            @NotNull @Min(1)
            Long pickedId
    ) {

    }

    public record Purchase(
            @NotNull @Min(1)
            Long answerId
    ) {
        public AnswerCommand.Purchase toCommand() {
            return AnswerCommand.Purchase.builder()
                    .answerId(this.answerId)
                    .build();
        }
    }
}
