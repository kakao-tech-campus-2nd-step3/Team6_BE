package supernova.whokie.user.controller.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.service.dto.UserModel;

import java.time.LocalDate;

public class UserResponse {


    @Builder
    public record PickedInfo(
            Long userId,
            String name,
            String imageUrl
    ) {
        public static UserResponse.PickedInfo from(UserModel.PickedInfo pickedInfo) {
            return PickedInfo.builder()
                    .userId(pickedInfo.userId())
                    .name(pickedInfo.name())
                    .imageUrl(pickedInfo.imageUrl())
                    .build();
        }

    }

    @Builder
    public record Info(
            String email,
            Gender gender,
            int age,
            String name,
            Role role,
            LocalDate createdAt
    ) {

        public static Info from(UserModel.Info user) {
            return Info.builder()
                    .email(user.email())
                    .gender(user.gender())
                    .age(user.age())
                    .name(user.name())
                    .role(user.role())
                    .createdAt(user.createdAt())
                    .build();
        }
    }

    @Builder
    public record Point(
            int amount
    ) {

        public static Point from(UserModel.Point user) {
            return Point.builder()
                    .amount(user.amount())
                    .build();
        }
    }
}
