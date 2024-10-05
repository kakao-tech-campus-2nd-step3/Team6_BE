package supernova.whokie.profile_question.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import supernova.whokie.profile_question.service.dto.ProfileQuestionCommand;

public class ProfileQuestionRequest {

    @Builder
    public record Create(
        @NotBlank
        String content
    ) {

        public ProfileQuestionCommand.Create toCommand() {
            return ProfileQuestionCommand.Create.builder()
                .content(content)
                .build();
        }
    }

}
