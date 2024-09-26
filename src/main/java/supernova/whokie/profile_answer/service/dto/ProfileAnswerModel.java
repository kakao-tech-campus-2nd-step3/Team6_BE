package supernova.whokie.profile_answer.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

public class ProfileAnswerModel {

    @Builder
    public record Info(
        Long profileAnswerId,
        String content,
        ProfileQuestionModel.Info profileQuestionInfo,
        LocalDateTime createdAt
    ) {

        public static Info from(ProfileAnswer profileAnswer) {
            ProfileQuestionModel.Info profileQuestionInfo = ProfileQuestionModel.Info.from(
                profileAnswer.getProfileQuestion());

            return Info.builder()
                .profileAnswerId(profileAnswer.getId())
                .content(profileAnswer.getContent())
                .profileQuestionInfo(profileQuestionInfo)
                .createdAt(profileAnswer.getCreatedAt())
                .build();
        }
    }

}
