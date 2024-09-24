package supernova.whokie.answer.controller.dto;

import lombok.Builder;
import supernova.whokie.answer.Answer;
import supernova.whokie.answer.service.AnswerService;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;

public class AnswerCommand {
    @Builder
    public record CommonAnswer(
            Long questionId,
            Long pickedId
    ) {
        public Answer toEntity(Question question, Users user, Users picked) {
            return Answer.create(question, user, picked, AnswerService.DEFAULT_HINT_COUNT);
        }
    }
}
