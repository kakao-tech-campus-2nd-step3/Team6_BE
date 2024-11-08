package supernova.whokie.answer.service.dto;

import java.time.LocalDate;
import lombok.Builder;
import supernova.whokie.answer.Answer;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.dto.UserModel;

import java.util.List;

public class AnswerModel {

    @Builder
    public record Refresh(
        List<UserModel.PickedInfo> users

    ) {

        public static AnswerModel.Refresh from(List<UserModel.PickedInfo> friendsInfoList) {
            return Refresh.builder()
                .users(friendsInfoList)
                .build();
        }

    }

    @Builder
    public record Hint(
        int hintNum,
        Boolean valid,
        String content
    ) {
        public static AnswerModel.Hint from(Answer answer, int hintCount, boolean valid) {
            return Hint.builder().hintNum(hintCount).valid(valid).content(answer.getPickerInfoByHintCount(hintCount, valid)).build();
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

        public static AnswerModel.Record from(Answer answer) {
            return new Record(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getQuestion().getContent(),
                answer.getHintCount(),
                answer.getCreatedAt().toLocalDate()
            );
        }
    }
    @Builder
    public record RecordDays(
            List<Integer> days
    ){
        public static AnswerModel.RecordDays from(List<Integer> answerRecordDays){
            return RecordDays.builder().days(answerRecordDays).build();
        }

    }
}
