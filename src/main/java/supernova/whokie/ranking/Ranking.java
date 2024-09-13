package supernova.whokie.ranking;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.user.Users;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ranking {

    @Id
    private Long id;

    private Long questionId;

    private Integer count;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private Long groupId;
}
