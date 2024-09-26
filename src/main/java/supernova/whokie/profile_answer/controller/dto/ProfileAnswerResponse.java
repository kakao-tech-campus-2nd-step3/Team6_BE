package supernova.whokie.profile_answer.controller.dto;

import java.time.LocalDateTime;
import lombok.Builder;

import java.time.LocalDate;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;
import supernova.whokie.profile_question.service.dto.ProfileQuestionModel;

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
