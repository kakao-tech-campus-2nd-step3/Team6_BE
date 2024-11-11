package supernova.whokie.user.service.dto;

import lombok.Builder;
import supernova.whokie.user.Gender;

import java.time.LocalDate;

public class UserCommand {

    @Builder
    public record Info(
            String name,
            Gender gender,
            LocalDate birthDate
    ) {

    }
}
