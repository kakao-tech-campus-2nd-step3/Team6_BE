package supernova.whokie.profile_question.service.dto;

import lombok.Builder;
import supernova.whokie.profile_question.ProfileQuestion;
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
