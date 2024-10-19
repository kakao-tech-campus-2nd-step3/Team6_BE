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
import supernova.whokie.answer.Answer;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.global.exception.InvalidEntityException;

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
    }

    public boolean hasNotEnoughPoint(int point) {
        return this.point < point;
    }

    public void decreasePoint(int point) {
        this.point -= point;
    }

    public void decreasePointsByHintCount(Answer answer) {
        switch (answer.getHintCount()) {
            case 1:
                checkUserHasNotEnoughPoint(Constants.FIRST_HINT_PURCHASE_POINT);
                decreasePoint(Constants.FIRST_HINT_PURCHASE_POINT);
                break;
            case 2:
                checkUserHasNotEnoughPoint(Constants.SECOND_HINT_PURCHASE_POINT);
                decreasePoint(Constants.SECOND_HINT_PURCHASE_POINT);
                break;
            case 3:
                checkUserHasNotEnoughPoint(Constants.THIRD_HINT_PURCHASE_POINT);
                decreasePoint(Constants.THIRD_HINT_PURCHASE_POINT);
                break;
        }
    }

    private void checkUserHasNotEnoughPoint(int point) {
        if (this.point < point) {
            throw new InvalidEntityException(MessageConstants.NOT_ENOUGH_POINT_MESSAGE);
        }
    }


}
