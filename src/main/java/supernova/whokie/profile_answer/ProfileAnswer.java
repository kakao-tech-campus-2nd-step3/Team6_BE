package supernova.whokie.profile_answer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.profile_question.ProfileQuestion;
import supernova.whokie.user.Users;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileAnswer extends BaseTimeEntity {

    @Id
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "profile_question_id")
    private ProfileQuestion profileQuestion;

    @ManyToOne
    @JoinColumn(name = "answered_user_id")
    private Users answeredUsers;

}
