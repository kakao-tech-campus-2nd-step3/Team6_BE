package supernova.whokie.question;

import jakarta.persistence.*;
import lombok.*;
import supernova.whokie.global.BaseTimeEntity;
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
