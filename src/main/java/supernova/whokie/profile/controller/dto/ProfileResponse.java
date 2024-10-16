package supernova.whokie.profile.controller.dto;

import lombok.Builder;
import supernova.whokie.profile.service.dto.ProfileModel;

public class ProfileResponse {

    @Builder
    public record Info(
            String description,
            String backgroundImageUrl,
            String name
    ) {

        public static ProfileResponse.Info from(ProfileModel.Info info) {
            return Info.builder()
                    .description(info.description())
                    .backgroundImageUrl(info.backgroundImageUrl())
                    .name(info.name())
                    .build();
        }
    }

    @Builder
    public record Visited(
            int todayVisited,
            int totalVisited
    ) {
        public static ProfileResponse.Visited from(ProfileModel.Visited visited) {
            return Visited.builder()
                    .todayVisited(visited.todayVisited())
                    .todayVisited(visited.totalVisited())
                    .build();
        }
    }
}
