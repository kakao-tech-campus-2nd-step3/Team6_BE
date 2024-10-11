package supernova.whokie.question.controller.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import supernova.whokie.group_member.controller.dto.GroupMemberResponse;
import supernova.whokie.group_member.service.dto.GroupMemberModel;
import supernova.whokie.question.Question;
import supernova.whokie.question.service.dto.QuestionModel;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.service.dto.UserModel;

import java.time.LocalDate;
import java.util.List;

public class QuestionResponse {

    @Builder
    public record GroupQuestions(
            List<GroupQuestion> questions
    ) {

        public static GroupQuestions from(List<QuestionModel.GroupQuestion> model) {
            return GroupQuestions.builder()
                .questions(
                    model.stream()
                    .map(GroupQuestion::from)
                    .toList()
                )
                .build();
        }
    }

    @Builder
    public record GroupQuestion(
            Long questionId,
            String content,
            List<GroupMemberResponse.Option> users
    ) {

        public static GroupQuestion from(QuestionModel.GroupQuestion model) {
            return GroupQuestion.builder()
                .questionId(model.questionId())
                .content(model.content())
                .users(
                    model.groupMembers().stream()
                    .map(GroupMemberResponse.Option::from)
                    .toList()
                )
                .build();
        }
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
            List<UserModel.PickedInfo> users
    ) {
        public static CommonQuestion from(Question question, List<UserModel.PickedInfo> friendList) {
            return CommonQuestion.builder()
                    .questionId(question.getId())
                    .content(question.getContent())
                    .users(friendList)
                    .build();
        }

    }
    @Builder
    public record Infos(
            Page<QuestionResponse.Info> infos
    ){
    public static Infos from(Page<QuestionModel.Info> infoList){
        return Infos.builder()
                .infos(
                        infoList.map(Info::from)
                )
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
        public static QuestionResponse.Info from(QuestionModel.Info info) {
            return Info.builder()
                    .questionId(info.questionId())
                    .questionContent(info.questionContent())
                    .groupId(info.groupId())
                    .status(info.status())
                    .writer(info.writer())
                    .createdAt(info.createdAt())
                    .build();
        }

    }
}
