package supernova.whokie.answer.controller.dto;

import lombok.Builder;
import supernova.whokie.user.controller.dto.UserResponse;

import java.util.List;

public class AnswerModel {

    @Builder
    public record Refresh(
            List<UserResponse.PickedInfo> users

    ){
        public static Refresh from(List<UserResponse.PickedInfo> friendsInfoList){
            return Refresh.builder()
                    .users(friendsInfoList)
                    .build();
        }

    }
}
