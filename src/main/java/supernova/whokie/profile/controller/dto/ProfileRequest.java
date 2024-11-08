package supernova.whokie.profile.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import supernova.whokie.profile.service.dto.ProfileCommand;

public class ProfileRequest {

    @Builder
    public record Modify(
            @NotBlank
            @NotNull
            String description
    ) {
        public ProfileCommand.Modify toCommand() {
            return ProfileCommand.Modify.builder()
                    .description(description)
                    .build();
        }
    }
}
