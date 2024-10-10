package supernova.whokie.friend;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import supernova.whokie.user.Users;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private Users hostUser;   // 나

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id")
    private Users friendUser; // 친구

    public Long getFriendUserId() {
        return friendUser.getId();
    }

    public static Friend create(Users hostUser,Users friendUser) {
        return Friend.builder()
                .hostUser(hostUser)
                .friendUser(friendUser)
                .build();
    }
}
