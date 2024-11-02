package supernova.whokie.friend.service.dto;

import lombok.Builder;
import supernova.whokie.user.Users;

public class FriendModel {

    @Builder
    public record Info(
            Long friendId,
            String name,
            String imageUrl,
            boolean isFriend
    ) {

        public static Info from(Users user, boolean isFriend, String imageUrl) {
            return Info.builder()
                    .friendId(user.getId())
                    .name(user.getName())
                    .imageUrl(imageUrl)
                    .isFriend(isFriend)
                    .build();
        }
    }
}
