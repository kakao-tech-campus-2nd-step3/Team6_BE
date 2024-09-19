package supernova.whokie.friend.controller.dto;

import lombok.Builder;

import java.util.List;

public class FriendResponse {

    @Builder
    public record Infos(
            List<Info> friends
    ) {

    }

    @Builder
    public record Info(
            Long friendId,
            String name,
            String imageUrl
    ) {

    }
}
