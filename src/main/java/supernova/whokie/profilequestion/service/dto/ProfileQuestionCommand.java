package supernova.whokie.profilequestion.service.dto;

import lombok.Builder;
import supernova.whokie.profilequestion.ProfileQuestion;
import supernova.whokie.user.Users;

public class ProfileQuestionCommand {

    @Builder
    public record Create(
            String content
    ) {

        public ProfileQuestion toEntity(Users user) {
            return ProfileQuestion.builder()
                    .content(content)
                    .user(user)
                    .profileQuestionStatus(true)
                    .build();
        }
    }

}
