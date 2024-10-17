package supernova.whokie.question.service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import supernova.whokie.friend.Friend;
import supernova.whokie.group_member.GroupMember;
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.user.service.dto.UserModel;

public class QuestionModel {

    @Builder
    public record CommonQuestion(
        Long questionId,
        String content,
        List<UserModel.PickedInfo> users
    ) {

        public static QuestionModel.CommonQuestion from(Question question,
            List<Friend> friendList) {
            List<UserModel.PickedInfo> users = friendList.stream()
                .map(friend -> UserModel.PickedInfo.from(friend.getFriendUser()))
                .toList();

            return CommonQuestion.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .users(users)
                .build();
        }

    }

    @Builder
    public record Info(
        Long questionId,
        String questionContent,
        Long groupId,
        QuestionStatus status,
        String writer,
        LocalDate createdAt
    ) {

        public static QuestionModel.Info from(Question question, QuestionStatus status) {
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

        public static QuestionModel.GroupQuestion from(Question question,
            List<GroupMember> groupMemberList) {
            List<GroupMemberModel.Option> groupMembers = groupMemberList.stream()
                .map(GroupMemberModel.Option::from)
                .toList();
            return GroupQuestion.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .groupMembers(groupMembers)
                .build();
        }
    }
}
