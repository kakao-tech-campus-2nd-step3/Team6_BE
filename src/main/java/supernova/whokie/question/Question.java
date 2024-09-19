package supernova.whokie.question;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.BaseTimeEntity;
import supernova.whokie.user.Users;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
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
