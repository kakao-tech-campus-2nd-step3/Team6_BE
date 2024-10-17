package supernova.whokie.profile;

import jakarta.persistence.*;
import lombok.*;
import supernova.whokie.user.Users;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private String description;

    private String backgroundImageUrl;

}
