package supernova.whokie.answer.controller.dto;

public class AnswerRequest {

    public record Common(
        Long questionId,
        Long pickedId
    ) {

    }

    public record Group(
        Long questionId,
        Long groupId,
        Long pickedId
    ) {

    }

    public record Purchase(
            Long answerId
    ) {

    }
}
