package supernova.whokie.profile_question;

import jakarta.persistence.Entity;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class ProfileQuestion extends BaseTimeEntity {

    @Id
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private Boolean profileQuestionStatus;
}
