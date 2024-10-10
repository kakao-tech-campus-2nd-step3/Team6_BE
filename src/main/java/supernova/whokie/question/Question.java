package supernova.whokie.question;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.user.Users;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus;

    private Long groupId; // group id

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Users writer;

    public boolean isNotCorrectGroupQuestion(Long groupId){
        return this.groupId != groupId;
    }


    public void changeStatus(Boolean status) {
        if (status) {
            questionStatus = QuestionStatus.APPROVED;
        } else {
            questionStatus = QuestionStatus.REJECTED;
        }
    }

    public static Question create(String content, QuestionStatus questionStatus, Long groupId, Users writer) {
        return Question.builder()
                .content(content)
                .questionStatus(questionStatus)
                .groupId(groupId)
                .writer(writer)
                .build();
    }
}
