package supernova.whokie.user.service.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

import java.time.LocalDate;

public class UserModel {

    @Builder
    public record PickedInfo(
            Long userId,
            String name,
            String imageUrl
    ) {
        public static PickedInfo from(Users user) {
            return PickedInfo.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .imageUrl(user.getImageUrl())
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

        public static UserModel.Info from(Users user) {
            return UserModel.Info.builder()
                    .email(user.getEmail())
                    .gender(user.getGender())
                    .age(user.getAge())
                    .name(user.getName())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt().toLocalDate())
                    .build();
        }
    }

    @Builder
    public record Point(
            int amount
    ) {

        public static UserModel.Point from(Users user) {
            return UserModel.Point.builder()
                    .amount(user.getPoint())
                    .build();
        }
    }
}
