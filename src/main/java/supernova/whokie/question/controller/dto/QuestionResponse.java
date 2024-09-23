package supernova.whokie.question.controller.dto;

import lombok.Builder;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;
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

    }

    @Builder
    public record CommonQuestion(
            Long questionId,
            String content,
            List<UserResponse.PickedInfo> users
    ) {

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
