package supernova.whokie.friend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.user.Users;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Friend {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_user_id")
    private Users hostUser;   // 나

    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private Users friendUser; // 친구
}
