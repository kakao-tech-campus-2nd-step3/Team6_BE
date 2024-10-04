package supernova.whokie.question.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class QuestionRequest {

    public record Create(
            @NotNull @Min(1)
            Long groupId,
            @NotNull
            String content
    ) {

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
