package supernova.whokie.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.user.constants.UserConstants;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class UsersTest {
    private List<Users> users;

    @BeforeEach
    void setUp() {
        users = createUsers();
    }

    @Test
    @DisplayName("나이ㅣ 구하기 테스트")
    void getAgeTest() {
        // given
        Users user = users.get(0);

        // when
        int actual = user.getAge();

        // then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("개인정보 입력 테스트")
    void updatePersonalInfoTest() {
        // given
        Users user = users.get(0);
        String name = "sodlfma";
        Gender gender = Gender.F;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        // when
        user.updatePersonalInfo(name, gender, birthDate);

        // then
        assertAll(
                () -> assertThat(user.getName()).isEqualTo(name),
                () -> assertThat(user.getGender()).isEqualTo(gender),
                () -> assertThat(user.getAge()).isEqualTo(Period.between(birthDate, LocalDate.now()).getYears())
        );
    }

    @Test
    @DisplayName("포인트 상승 테스트")
    void increasePointTest() {
        // given
        Users user = users.get(0);
        int pre = user.getPoint();
        int point = 10;

        // when
        user.increasePoint(point);

        // then
        assertThat(user.getPoint()).isEqualTo(pre + point);
    }

    @Test
    @DisplayName("포인트 상승 테스트")
    void decreasePointTest() {
        // given
        Users user = users.get(3);
        int pre = user.getPoint();
        int point = 10;

        // when
        user.decreasePoint(point);

        // then
        assertThat(user.getPoint()).isEqualTo(pre - point);
    }

    @Test
    @DisplayName("힌트 카운트에 따라 포인트 감소 테스트 성공")
    void decreasePointsByHintCountTest() {
        // given
        Users user = users.get(0);
        int prePoint = user.getPoint();
        int hintCount = 2;
        int decreasedPoint = UserConstants.THIRD_HINT_PURCHASE_POINT;

        // when
        user.decreasePointsByHintCount(hintCount);

        // then
        assertThat(user.getPoint()).isEqualTo(prePoint - decreasedPoint);
    }

    @Test
    @DisplayName("포인트 부족 체크 테스트")
    void checkUserHasNotEnoughPointTest() {
        // given
        Users user = users.get(3);
        int hintCount = 1;

        // when
        // then
        assertThatExceptionOfType(InvalidEntityException.class)
                .isThrownBy(() -> user.decreasePointsByHintCount(hintCount));
    }

    @Test
    @DisplayName("Set에 UserId가 포함되는지 테스트")
    void isFriendTest() {
        // given
        Users user1 = users.get(0);
        Users user2 = users.get(1);
        Users user3 = users.get(2);
        Set<Long> set = Set.of(user1.getId(), user2.getId());

        // when
        boolean actual1 = set.contains(user1.getId());
        boolean actual2 = set.contains(user3.getId());

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("name").email("email1").point(100).birthDate(LocalDate.now())
                .kakaoId(1L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("name").email("email2").point(0).birthDate(LocalDate.now())
                .kakaoId(2L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("name").email("email3").point(0).birthDate(LocalDate.now())
                .kakaoId(3L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user4 = Users.builder().id(4L).name("name").email("email4").point(0).birthDate(null)
                .kakaoId(4L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        return List.of(user1, user2, user3, user4);
    }
}