package supernova.whokie.answer.service.dto;

import lombok.Builder;
import supernova.whokie.answer.Answer;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;

public class AnswerCommand {
    @Builder
    public record CommonAnswer(
            Long questionId,
            Long pickedId
    ) {
        public Answer toEntity(Question question, Users user, Users picked, int hintCount) {
            return Answer.create(question, user, picked, hintCount);
        }
    }

    @Builder
    public record Purchase(
            Long answerId
    ){

    }
}
