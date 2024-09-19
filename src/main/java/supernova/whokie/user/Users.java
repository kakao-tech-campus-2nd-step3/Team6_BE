package supernova.whokie.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String email;
    private Integer point;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

}
