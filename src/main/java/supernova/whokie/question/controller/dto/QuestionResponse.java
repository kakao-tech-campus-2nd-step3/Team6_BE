package supernova.whokie.question.controller.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import supernova.whokie.question.QuestionStatus;
import supernova.whokie.question.service.dto.QuestionModel;

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
            String content
    ) {

        public static GroupQuestion from(QuestionModel.GroupQuestion model) {
            return GroupQuestion.builder()
                    .questionId(model.questionId())
                    .content(model.content())
                    .build();
        }
    }

    @Builder
    public record CommonQuestions(
            List<CommonQuestion> questions
    ) {
        public static CommonQuestions from(List<QuestionModel.CommonQuestion> models) {
            List<CommonQuestion> commonQuestions = models.stream().map(CommonQuestion::from).toList();
            return CommonQuestions.builder()
                    .questions(commonQuestions)
                    .build();
        }

    }

    @Builder
    public record CommonQuestion(
            Long questionId,
            String content
    ) {
        public static CommonQuestion from(QuestionModel.CommonQuestion question) {
            return CommonQuestion.builder()
                    .questionId(question.questionId())
                    .content(question.content())
                    .build();
        }

    }

    @Builder
    public record Infos(
            Page<QuestionResponse.Info> infos
    ) {
        public static Infos from(Page<QuestionModel.Info> infoList) {
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
            QuestionStatus status,
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
