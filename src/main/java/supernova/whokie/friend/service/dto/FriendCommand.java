package supernova.whokie.friend.service.dto;

import lombok.Builder;
import supernova.whokie.friend.Friend;
import supernova.whokie.user.Users;

import java.util.List;

public class FriendCommand {

    @Builder
    public record Update(
        List<Long> friendIds
    ) {
        public List<Friend> toEntity(Users host, List<Users> friendUsers) {
            return friendUsers.stream()
                    .map(friendUser -> Friend.builder()
                            .hostUser(host)
                            .friendUser(friendUser)
                            .build()
                    ).toList();
        }
    }
}
