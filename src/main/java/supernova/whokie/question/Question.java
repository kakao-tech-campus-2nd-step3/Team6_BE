package supernova.whokie.question;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus;

    private Long groupId; // group id

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Users writer;


}
