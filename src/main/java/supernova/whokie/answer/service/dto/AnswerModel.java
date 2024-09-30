package supernova.whokie.answer.service.dto;

import lombok.Builder;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.dto.UserModel;

import java.util.List;

public class AnswerModel {

    @Builder
    public record Refresh(
            List<UserModel.PickedInfo> users

    ) {
        public static AnswerModel.Refresh from(List<UserModel.PickedInfo> friendsInfoList) {
            return Refresh.builder()
                    .users(friendsInfoList)
                    .build();
        }

    }

    @Builder
    public record Hint(
            int hintNum,
            Boolean valid,
            String content
    ){
        public static AnswerModel.Hint from(Users user, int hintCount, boolean valid){
            return switch (hintCount) {
                case 1 -> Hint.builder().hintNum(1).valid(valid).content(String.valueOf(user.getGender())).build();
                case 2 -> Hint.builder().hintNum(2).valid(valid).content(String.valueOf(user.getAge())).build();
                case 3 -> Hint.builder().hintNum(3).valid(valid).content(user.getName()).build();
                default -> throw new IllegalArgumentException("유효하지 않은 hintCount입니다. ");
            };
        }

    }
}
