package supernova.whokie.question.service.dto;

import lombok.Builder;
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.question.Question;
import supernova.whokie.user.service.dto.UserModel;

import java.time.LocalDate;
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

    @Builder
    public record Info(
            Long questionId,
            String questionContent,
            Long groupId,
            Boolean status,
            String writer,
            LocalDate createdAt
    ) {
        public static QuestionModel.Info from(Question question, Boolean status) {
            return Info.builder()
                    .questionId(question.getId())
                    .questionContent(question.getContent())
                    .groupId(question.getGroupId())
                    .status(status)
                    .writer(question.getWriter().getName())
                    .createdAt(question.getCreatedAt().toLocalDate())
                    .build();

        }
    }

    @Builder
    public record GroupQuestion(
            Long questionId,
            String content,
            List<GroupMemberModel.Option> groupMembers
    ) {
        public static QuestionModel.GroupQuestion from(Question question, List<GroupMemberModel.Option> groupMemberList) {
            return GroupQuestion.builder()
                    .questionId(question.getId())
                    .content(question.getContent())
                    .groupMembers(groupMemberList)
                    .build();
        }
    }
}
