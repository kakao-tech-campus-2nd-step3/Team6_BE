package supernova.whokie.user;

import jakarta.persistence.*;
import lombok.*;
import supernova.whokie.global.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
@Getter
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Integer point;
    private Integer age;
    private String kakaoCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;
}
