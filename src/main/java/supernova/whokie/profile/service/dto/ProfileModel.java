package supernova.whokie.profile.service.dto;

import lombok.Builder;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.ProfileVisitCount;

public class ProfileModel {

    @Builder
    public record Info(
            String description,
            String backgroundImageUrl,
            String name
    ) {

        public static ProfileModel.Info from(Profile profile) {
            return Info.builder()
                    .description(profile.getDescription())
                    .backgroundImageUrl(profile.getBackgroundImageUrl())
                    .name(profile.getUsers().getName())
                    .build();
        }
    }

    @Builder
    public record Visited(
            int todayVisited,
            int totalVisited
    ) {
        public static ProfileModel.Visited from(ProfileVisitCount visitCount) {
            return Visited.builder()
                    .todayVisited(visitCount.getDailyVisited())
                    .totalVisited(visitCount.getTotalVisited())
                    .build();
        }
    }
}
