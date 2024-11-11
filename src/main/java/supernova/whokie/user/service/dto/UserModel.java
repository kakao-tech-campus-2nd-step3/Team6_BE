package supernova.whokie.user.service.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;

public class UserModel {

    @Builder
    public record PickedInfo(
            Long userId,
            String name,
            String imageUrl
    ) {
        public static PickedInfo from(Users user, String imageUrl) {
            return PickedInfo.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .imageUrl(imageUrl)
                    .build();
        }

    }

    @Builder
    public record Info(
            Long id,
            String name,
            String imageUrl,
            String email,
            Gender gender,
            int point,
            int age,
            Role role,
            Long kakaoId,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {

        public static UserModel.Info from(Users user) {
            return UserModel.Info.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .imageUrl(user.getImageUrl())
                    .email(user.getEmail())
                    .gender(user.getGender())
                    .point(user.getPoint())
                    .age(user.getAge())
                    .role(user.getRole())
                    .kakaoId(user.getKakaoId())
                    .createdAt(user.getCreatedAt())
                    .modifiedAt(user.getModifiedAt())
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

    @Builder
    public record Login(
            String jwt,
            Long userId,
            Role role
    ) {
        public static UserModel.Login from(String jwt, Long userId, Role role) {
            return Login.builder()
                    .jwt(jwt)
                    .userId(userId)
                    .role(role)
                    .build();
        }
    }
}
