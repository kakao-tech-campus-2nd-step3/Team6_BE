package supernova.whokie.question.controller.dto;

import lombok.Builder;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;
import supernova.whokie.question.Question;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.controller.dto.UserResponse;

import java.time.LocalDate;
import java.util.List;

public class QuestionResponse {

    @Builder
    public record GroupQuestions(
            List<GroupQuestion> questions
    ) {

    }

    @Builder
    public record GroupQuestion(
            Long questionId,
            String content,
            List<GroupMemberResponse.Option> users
    ) {

    }

    @Builder
    public record CommonQuestions(
            List<CommonQuestion> questions
    ) {
        public static CommonQuestions from(List<QuestionModel.CommonQuestion> commonQuestions) {
            return CommonQuestions.builder()
                    .questions(
                            commonQuestions.stream().map(
                                    commonQuestion -> CommonQuestion.builder()
                                            .questionId(commonQuestion.questionId())
                                            .content(commonQuestion.content())
                                            .users(commonQuestion.users())
                                            .build()
                            ).toList()).build();
        }

    }

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

    @Builder
    public record Info(
            Long questionId,
            String questionContent,
            Long groupId,
            Boolean status,
            String writer,
            LocalDate createdAt
    ) {

    }
}
