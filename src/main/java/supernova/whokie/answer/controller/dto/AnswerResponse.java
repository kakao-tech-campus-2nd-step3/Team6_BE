package supernova.whokie.answer.controller.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import supernova.whokie.answer.service.dto.AnswerModel;
import supernova.whokie.user.service.dto.UserModel;

public class AnswerResponse {

    @Builder
    public record Refresh(
        List<UserModel.PickedInfo> users
    ) {

        public static AnswerResponse.Refresh from(AnswerModel.Refresh refresh) {
            return Refresh.builder()
                .users(refresh.users())
                .build();
        }

    }

    @Builder
    public record Record(
        Long answerId,
        Long questionId,
        String questionContent,
        int hintCount,
        LocalDate createdAt
    ) {

        public static AnswerResponse.Record from(AnswerModel.Record model) {
            return new Record(
                model.answerId(),
                model.questionId(),
                model.questionContent(),
                model.hintCount(),
                model.createdAt()
            );
        }
    }

    @Builder
    public record Hints(
        List<AnswerResponse.Hint> hints
    ) {

        public static AnswerResponse.Hints from(List<AnswerModel.Hint> hintList) {
            List<AnswerResponse.Hint> hints = hintList.stream().map(
                hint -> Hint.builder()
                    .hintNum(hint.hintNum())
                    .valid(hint.valid())
                    .content(hint.content())
                    .build()).toList();
            return Hints.builder()
                .hints(hints).build();
        }

    }

    @Builder
    public record Hint(
        int hintNum,
        Boolean valid,
        String content
    ) {

    }
}
