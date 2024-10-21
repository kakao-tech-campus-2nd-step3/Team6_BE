package supernova.whokie.profile.service.dto;

import lombok.Builder;
import supernova.whokie.profile.Profile;
import supernova.whokie.redis.entity.RedisVisitCount;

public class ProfileModel {

    @Builder
    public record Info(
            String description,
            String backgroundImageUrl,
            String name,
            int todayVisited,
            int totalVisited
    ) {

        public static ProfileModel.Info from(Profile profile, RedisVisitCount visitCount) {
            return Info.builder()
                    .description(profile.getDescription())
                    .backgroundImageUrl(profile.getBackgroundImageUrl())
                    .name(profile.getUsers().getName())
                    .todayVisited(visitCount.getDailyVisited())
                    .totalVisited(visitCount.getTotalVisited())
                    .build();
        }
    }
}
