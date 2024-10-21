package supernova.whokie.friend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd",
    "spring.sql.init.mode=never"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FriendRepositoryTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    private List<Users> users;

    @BeforeEach
    void setUp() {
        users = createUsers();
    }

    @Test
    @DisplayName("hostId로 모든 Friend 조회")
    void findByHostUserIdFetchJoinTest() {
        // given
        Users host = users.get(0);
        Users user1 = users.get(1);
        Users user2 = users.get(2);

        Friend friend1 = Friend.builder().id(1L).hostUser(host).friendUser(user1).build();
        Friend friend2 = Friend.builder().id(2L).hostUser(user1).friendUser(user2).build();
        friendRepository.saveAll(List.of(friend1, friend2));

        // when
        List<Friend> actual = friendRepository.findByHostUserIdFetchJoin(host.getId());

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(friend1.getId());
        assertThat(actual.getFirst().getFriendUser().getId()).isEqualTo(user1.getId());
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("host").email("host").point(1).age(1).kakaoId(1L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("user1").email("user1").point(1).age(1).kakaoId(2L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("user2").email("user2").point(1).age(1).kakaoId(3L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        return userRepository.saveAll(List.of(user1, user2, user3));
    }
}