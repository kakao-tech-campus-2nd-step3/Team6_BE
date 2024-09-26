package supernova.whokie.answer.service.dto;

import lombok.Builder;
import lombok.Getter;
import supernova.whokie.answer.Answer;

import java.time.LocalDate;

@Builder
@Getter
public class AnswerRecord {
    private Long answerId;
    private Long questionId;
    private String questionContent;
    private int hintCount;
    private LocalDate createdAt;

    public static AnswerRecord from(Answer answer) {
        return AnswerRecord.builder()
                .answerId(answer.getId())
                .questionId(answer.getQuestion().getId())
                .questionContent(answer.getQuestion().getContent())
                .hintCount(answer.getHintCount())
                .createdAt(answer.getCreatedAt().toLocalDate())
                .build();
    }
}
