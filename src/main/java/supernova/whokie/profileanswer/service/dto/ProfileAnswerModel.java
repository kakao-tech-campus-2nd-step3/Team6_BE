package supernova.whokie.profileanswer.service.dto;

import lombok.Builder;
import supernova.whokie.profileanswer.ProfileAnswer;
import supernova.whokie.profilequestion.service.dto.ProfileQuestionModel;

import java.time.LocalDateTime;

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
