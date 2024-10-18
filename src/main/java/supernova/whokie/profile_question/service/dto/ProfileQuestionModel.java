package supernova.whokie.profile_question.service.dto;

import lombok.Builder;
import supernova.whokie.profile_question.ProfileQuestion;

import java.time.LocalDateTime;
import java.util.List;


public class ProfileQuestionModel {

    @Builder
    public record Info(
            Long id,
            String content,
            LocalDateTime createdAt
    ) {

        public static Info from(ProfileQuestion profileQuestion) {
            return Info.builder()
                    .id(profileQuestion.getId())
                    .content(profileQuestion.getContent())
                    .createdAt(profileQuestion.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record InfoList(
            List<Info> infoList
    ) {

        public static InfoList from(List<ProfileQuestion> profileQuestions) {
            return InfoList.builder()
                    .infoList(profileQuestions.stream().map(Info::from).toList())
                    .build();
        }
    }
}
