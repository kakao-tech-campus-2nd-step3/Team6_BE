package supernova.whokie.profile.service.dto;

import lombok.Builder;
import supernova.whokie.profile.Profile;

public class ProfileModel {

    @Builder
    public record Info(
        int todayVisited,
        int totalVisited,
        String description,
        String backgroundImageUrl,
        String name
    ) {

        public static ProfileModel.Info from(Profile profile) {
            return Info.builder()
                .todayVisited(profile.getTodayVisited())
                .totalVisited(profile.getTotalVisited())
                .description(profile.getDescription())
                .backgroundImageUrl(profile.getBackgroundImageUrl())
                .name(profile.getUsers().getName())
                .build();
        }
    }
}
