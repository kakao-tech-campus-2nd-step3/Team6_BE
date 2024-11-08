package supernova.whokie.profile.service.dto;

import lombok.Builder;

public class ProfileCommand {

    @Builder
    public record Modify(
            String description
    ) {

    }
}
