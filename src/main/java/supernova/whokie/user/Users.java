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
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.user.constants.UserConstants;

import java.time.LocalDate;
import java.time.Period;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Min(0)
    private Integer point;

//    @NotNull
    private LocalDate birthDate;

    @NotNull
    private Long kakaoId;

//    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    public int getAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    public void updatePersonalInfo(String name, Gender gender, LocalDate birthDate) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.role = Role.USER;
    }

    public void increasePoint(int point) {
        this.point += point;
    }

    public void decreasePoint(int point) {
        this.point -= point;
    }

    public int decreasePointsByHintCount(Answer answer) {
        int decreasedPoint = 0;
        switch (answer.getHintCount()) {
            case 0:
                decreasedPoint = UserConstants.FIRST_HINT_PURCHASE_POINT;
                checkUserHasNotEnoughPoint(decreasedPoint);
                decreasePoint(decreasedPoint);
                break;
            case 1:
                decreasedPoint = UserConstants.SECOND_HINT_PURCHASE_POINT;
                checkUserHasNotEnoughPoint(decreasedPoint);
                decreasePoint(decreasedPoint);
                break;
            case 2:
                decreasedPoint = UserConstants.THIRD_HINT_PURCHASE_POINT;
                checkUserHasNotEnoughPoint(decreasedPoint);
                decreasePoint(decreasedPoint);
                break;
            default:
                throw new InvalidEntityException(MessageConstants.ALL_HINT_USED_MESSAGE);
        }
        return decreasedPoint;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isImageUrlStoredInS3() {
        return imageUrl.equals(UserConstants.USER_IMAGE_FOLRDER + "/" + id + ".png");
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private void checkUserHasNotEnoughPoint(int point) {
        if (this.point < point) {
            throw new InvalidEntityException(MessageConstants.NOT_ENOUGH_POINT_MESSAGE);
        }
    }


}
