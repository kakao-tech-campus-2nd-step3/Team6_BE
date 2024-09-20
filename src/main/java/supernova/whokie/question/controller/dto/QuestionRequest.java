package supernova.whokie.question.controller.dto;

public class QuestionRequest {

    public record Create(
            Long groupId,
            String content
    ) {

    }

    public record Approve(
            Long groupId,
            Long questionId,
            Boolean status
    ) {

    }
}
