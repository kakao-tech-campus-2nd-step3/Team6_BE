package supernova.whokie.question.service.dto;

import lombok.Builder;
import supernova.whokie.question.Question;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.dto.UserModel;

import java.util.List;

public class QuestionModel {
    @Builder
    public record CommonQuestion(
            Long questionId,
            String content,
            List<UserModel.PickedInfo> users
    ) {
        public static QuestionModel.CommonQuestion from(Question question, List<UserModel.PickedInfo> friendList) {
            return CommonQuestion.builder()
                    .questionId(question.getId())
                    .content(question.getContent())
                    .users(friendList)
                    .build();
        }

    }
}
