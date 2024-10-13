package supernova.whokie.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public static Users create(String name, String email, Integer point, Integer age, Long kakaoId, Gender gender, String imageUrl, Role role) {
        return Users.builder()
                .name(name)
                .email(email)
                .point(point)
                .age(age)
                .kakaoId(kakaoId)
                .gender(gender)
                .imageUrl(imageUrl)
                .role(role)
                .build();
    }

    public void increasePoint(int point) {
        this.point += point;
    }

    public boolean hasNotEnoughPoint(int point) {
        return this.point < point;
    }

    public void decreasePoint(int point) {
        this.point -= point;
    }
}
