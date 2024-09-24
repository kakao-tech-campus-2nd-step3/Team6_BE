package supernova.whokie.answer.controller.dto;

public class AnswerRequest {

    public record Common(
            Long questionId,
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
            Long answerId
    ) {

    }
}
