package supernova.whokie.profile_answer.controller.dto;

import lombok.Builder;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;

import java.time.LocalDateTime;

public class ProfileAnswerResponse {

    @Builder
    public record Answer(
            Long profileAnswerId,
            String content,
            String profileQuestionContent,
            LocalDateTime createdAt
    ) {

        public static Answer from(ProfileAnswerModel.Info model) {
            return new Answer(
                    model.profileAnswerId(),
                    model.content(),
                    model.profileQuestionInfo().content(),
                    model.createdAt()
            );
        }
    }
}
