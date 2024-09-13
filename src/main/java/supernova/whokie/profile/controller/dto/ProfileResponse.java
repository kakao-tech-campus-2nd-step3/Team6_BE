package supernova.whokie.profile.controller.dto;

import lombok.Builder;

public class ProfileResponse {

    @Builder
    public record Info(
            int todayVisited,
            int totalVisited,
            String description,
            String backgroundImageUrl,
            String name
    ) {

    }
}
