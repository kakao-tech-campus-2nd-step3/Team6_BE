package supernova.whokie.user.controller.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;

import java.time.LocalDate;
import supernova.whokie.user.Users;

public class UserResponse {


    @Builder
    public record PickedInfo(
        Long userId,
        String name,
        String imageUrl
    ) {
        public static PickedInfo from(Users user){
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

        public static Info from(Users user) {
            return Info.builder()
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

        public static Point from(Users user) {
            return Point.builder()
                .amount(user.getPoint())
                .build();
        }
    }
}
