package supernova.whokie.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.user.Users;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Profile {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private Integer todayVisited;

    private Integer totalVisited;

    private String description;

    private String backgroundImageUrl;

}
