package supernova.whokie.user.controller.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;

import java.time.LocalDate;

public class UserResponse {


    @Builder
    public record PickedInfo(
        Long userId,
        String name,
        String imageUrl
    ) {

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

    }

    @Builder
    public record Point(
            int amount
    ) {

    }
}
