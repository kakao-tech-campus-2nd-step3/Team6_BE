package supernova.whokie.answer;

import jakarta.persistence.*;
import lombok.*;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // question id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picker_id")
    private Users picker; // picker id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picked_id")
    private Users picked; // picked id

    private Integer hintCount;

    public static Answer create(Question question, Users picker, Users picked, Integer hintCount) {
        return Answer.builder()
                .question(question)
                .picker(picker)
                .picked(picked)
                .hintCount(hintCount)
                .build();
    }

}
