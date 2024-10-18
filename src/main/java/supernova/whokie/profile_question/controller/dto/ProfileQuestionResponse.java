package supernova.whokie.profile_question.controller.dto;

import lombok.Builder;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

import java.time.LocalDate;
import java.util.List;

public class ProfileQuestionResponse {

    @Builder
    public record Questions(
            List<Question> questions
    ) {

        public static Questions from(ProfileQuestionModel.InfoList infoList) {
            return Questions.builder()
                    .questions(infoList.infoList().stream().map(Question::from).toList())
                    .build();
        }
    }

    @Builder
    public record Question(
            Long profileQuestionId,
            String profileQuestionContent,
            LocalDate createdAt
    ) {

        public static Question from(ProfileQuestionModel.Info info) {
            return Question.builder()
                    .profileQuestionId(info.id())
                    .profileQuestionContent(info.content())
                    .createdAt(info.createdAt().toLocalDate())
                    .build();
        }
    }
}
