package supernova.whokie.answer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import supernova.whokie.global.BaseTimeEntity;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
public class Answer extends BaseTimeEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question; // question id

    @ManyToOne
    @JoinColumn(name = "picker_id")
    private Users picker; // picker id

    @ManyToOne
    @JoinColumn(name = "picked_id")
    private Users picked; // picked id

    private Integer hintCount;

    public void configCreatedAt(LocalDateTime localDateTime){
        super.configCreatedAt(localDateTime);
    }
}
