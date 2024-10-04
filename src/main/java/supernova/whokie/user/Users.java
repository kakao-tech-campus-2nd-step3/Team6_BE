package supernova.whokie.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import supernova.whokie.global.entity.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Min(0)
    private Integer point;

    @NotNull
    private Integer age;

    @NotNull
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    public void increasePoint(int point) {
        this.point += point;
    public boolean hasNotEnoughPoint(int point){
        return this.point < point;
    }

    public void decreasePoint(int point) {
        this.point -= point;
    }
}
