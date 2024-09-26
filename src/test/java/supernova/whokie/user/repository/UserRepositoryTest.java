package supernova.whokie.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.user.Users;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("id리스트로 Users 조회")
    void findByIdInTest() {
        // given
        Users user1 = Users.builder().id(1L).build();
        Users user2 = Users.builder().id(2L).build();
        Users user3 = Users.builder().id(3L).build();
        List<Users> users = List.of(user1, user2, user3);
        List<Long> ids = users.stream().map(Users::getId).toList();
        List<Users> savedUsers = List.of(user1, user2);
        userRepository.saveAll(savedUsers);

        // when
        List<Users> actual = userRepository.findByIdIn(ids);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getId()).isEqualTo(user1.getId());
        assertThat(actual.get(1).getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("kakaoCode리스트로 Users 조회")
    void findByKakaoCodeInTest() {
        // given
        Users user1 = Users.builder().id(1L).kakaoId("user1").build();
        Users user2 = Users.builder().id(2L).kakaoId("user2").build();
        Users user3 = Users.builder().id(3L).kakaoId("user3").build();
        List<Users> users = List.of(user1, user2, user3);
        List<String> kakaoCodes = users.stream().map(Users::getKakaoId).toList();
        List<Users> savedUsers = List.of(user1, user2);
        userRepository.saveAll(savedUsers);

        // when
        List<Users> actual = userRepository.findByKakaoCodeIn(kakaoCodes);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getId()).isEqualTo(user1.getId());
        assertThat(actual.get(1).getId()).isEqualTo(user2.getId());
    }
}