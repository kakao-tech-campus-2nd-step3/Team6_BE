package supernova.whokie.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private List<Users> users;

    @BeforeEach
    void setUp() {
        users = createUsers();
    }

    @Test
    @DisplayName("id리스트로 Users 조회")
    void findByIdInTest() {
        // given
        Users user1 = users.get(0);
        Users user2 = users.get(1);
        Users user3 = users.get(2);
        List<Users> users = List.of(user1, user2, user3);
        List<Long> ids = users.stream().map(Users::getId).toList();

        // when
        List<Users> actual = userRepository.findByIdIn(ids);

        // then
        assertAll(
            () -> assertThat(actual).hasSize(3),
            () -> assertThat(actual.get(0).getId()).isEqualTo(user1.getId()),
            () -> assertThat(actual.get(1).getId()).isEqualTo(user2.getId()),
            () -> assertThat(actual.get(2).getId()).isEqualTo(user3.getId())
        );
    }

    @Test
    @DisplayName("kakaoCode리스트로 Users 조회")
    void findByKakaoIdInTest() {
        // given
        Users user1 = users.get(0);
        Users user2 = users.get(1);
        Users user3 = users.get(2);
        List<Users> users = List.of(user1, user2, user3);
        List<Long> kakaoIds = users.stream().map(Users::getKakaoId).toList();

        // when
        List<Users> actual = userRepository.findByKakaoIdIn(kakaoIds);

        // then
        assertAll(
            () -> assertThat(actual).hasSize(3),
            () -> assertThat(actual.get(0).getId()).isEqualTo(user1.getId()),
            () -> assertThat(actual.get(1).getId()).isEqualTo(user2.getId()),
            () -> assertThat(actual.get(2).getId()).isEqualTo(user3.getId())
        );
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("name").email("email2").point(0).age(1)
            .kakaoId(2L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("name").email("email3").point(0).age(1)
            .kakaoId(3L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("name").email("email4").point(0).age(1)
            .kakaoId(4L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        return userRepository.saveAll(List.of(user1, user2, user3));
    }
}