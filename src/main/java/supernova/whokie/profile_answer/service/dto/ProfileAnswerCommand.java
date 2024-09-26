package supernova.whokie.profile_answer.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.user.Users;

public class ProfileAnswerCommand {

    @Builder
    public record Create(
        @NotNull
        Long profileQuestionId,
        @NotBlank
        String content
    ) {

        public ProfileAnswer toEntity(Users answeredUser, ProfileQuestion profileQuestion) {
            return ProfileAnswer.builder()
                .content(content)
                .profileQuestion(profileQuestion)
                .answeredUser(answeredUser)
                .build();
        }
    }
}
