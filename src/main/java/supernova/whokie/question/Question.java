package supernova.whokie.question;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import supernova.whokie.answer.Answer;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.user.Users;

import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    private Long groupId; // group id

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Users writer;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();


    public boolean isNotCorrectGroupQuestion(Long groupId) {
        return this.groupId != groupId;
    }

    public void changeStatus(Boolean status) {
        if (status) {
            questionStatus = QuestionStatus.APPROVED;
        } else {
            questionStatus = QuestionStatus.REJECTED;
        }
    }
}
