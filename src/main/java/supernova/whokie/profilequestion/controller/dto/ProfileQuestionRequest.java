package supernova.whokie.profilequestion.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import supernova.whokie.profilequestion.service.dto.ProfileQuestionCommand;

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
