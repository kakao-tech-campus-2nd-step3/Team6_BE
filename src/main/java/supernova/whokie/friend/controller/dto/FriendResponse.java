package supernova.whokie.friend.controller.dto;

import lombok.Builder;
import supernova.whokie.friend.service.dto.FriendModel;

import java.util.List;

public class FriendResponse {

    @Builder
    public record Infos(
            List<FriendResponse.Info> friends
    ) {
        public static Infos from(List<FriendModel.Info> infos) {
            return Infos.builder()
                    .friends(infos.stream().map(
                            info -> Info.builder()
                                    .friendId(info.friendId())
                                    .name(info.name())
                                    .imageUrl(info.imageUrl())
                                    .isFriend(info.isFriend())
                                    .build()
                    ).toList()).build();
        }
    }

    @Builder
    public record Info(
            Long friendId,
            String name,
            String imageUrl,
            boolean isFriend
    ) {

    }
}
