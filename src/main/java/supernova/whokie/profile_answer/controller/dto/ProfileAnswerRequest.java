package supernova.whokie.profile_answer.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerCommand;

public class ProfileAnswerRequest {

    @Builder
    public record Answer(
            @NotNull
            String content,
            @NotNull @Min(1)
            Long profileQuestionId
    ) {

        public ProfileAnswerCommand.Create toCommand() {
            return ProfileAnswerCommand.Create.builder()
                    .content(content())
                    .profileQuestionId(profileQuestionId())
                    .build();
        }
    }
}
