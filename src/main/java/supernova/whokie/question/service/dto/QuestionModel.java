package supernova.whokie.question.service.dto;

import lombok.Builder;
import supernova.whokie.question.Question;
import supernova.whokie.user.controller.dto.UserResponse;

import java.util.List;

public class QuestionModel {
    @Builder
    public record CommonQuestion(
            Long questionId,
            String content,
            List<UserResponse.PickedInfo> users
    ) {
        public static CommonQuestion from(Question question, List<UserResponse.PickedInfo> friendList) {
            return CommonQuestion.builder()
                    .questionId(question.getId())
                    .content(question.getContent())
                    .users(friendList)
                    .build();
        }

    }
}
