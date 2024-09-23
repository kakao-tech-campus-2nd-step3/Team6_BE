package supernova.whokie.friend.service.dto;

import lombok.Builder;
import supernova.whokie.user.Users;

public class FriendModel {

    @Builder
    public record Info(
            Long friendId,
            String name,
            String imageUrl
    ) {

        public static Info from(Users users) {
            return Info.builder()
                    .friendId(users.getId())
                    .name(users.getName())
                    .imageUrl(users.getImageUrl())
                    .build();
        }
    }
}
