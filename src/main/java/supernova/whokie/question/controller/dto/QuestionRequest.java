package supernova.whokie.question.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import supernova.whokie.question.service.dto.QuestionCommand;

public class QuestionRequest {

    public record Create(
            @NotNull @Min(1)
            Long groupId,
            @NotNull
            String content
    ) {

        public QuestionCommand.Create toCommand() {
            return QuestionCommand.Create.builder()
                .groupId(groupId)
                .content(content)
                .build();
        }
    }

    public record Approve(
            @NotNull @Min(1)
            Long groupId,
            @NotNull @Min(1)
            Long questionId,
            @NotNull
            Boolean status
    ) {

    }
}
