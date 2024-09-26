package supernova.whokie.user.service.dto;

import lombok.Builder;
import supernova.whokie.user.Users;

public class UserModel {
    @Builder
    public record PickedInfo(
            Long userId,
            String name,
            String imageUrl

    ){
        public static PickedInfo from(Users user){
            return PickedInfo.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .imageUrl(user.getImageUrl())
                    .build();
        }


    }
}
