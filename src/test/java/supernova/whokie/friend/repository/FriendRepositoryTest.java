package supernova.whokie.friend.repository;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd",
    "spring.sql.init.mode=never"
})
@MockBean({S3Client.class, S3Template.class, S3Presigner.class, RedissonClient.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FriendRepositoryTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;

    private List<Users> users;
    private List<Friend> friends;

    @BeforeEach
    void setUp() {
        users = createUsers();
        friends = createFriends();
    }

    @Test
    @DisplayName("hostId로 모든 Friend 조회")
    void findByHostUserIdFetchJoinTest() {
        // given
        Users host = users.get(0);
        Users user1 = users.get(1);

        Friend friend1 = friends.get(0);
        Friend friend2 = friends.get(1);
        friendRepository.saveAll(List.of(friend1, friend2));

        // when
        List<Friend> actual = friendRepository.findByHostUserIdFetchJoin(host.getId());

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(friend1.getId());
        assertThat(actual.getFirst().getFriendUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("hostUser로 모든 Friend 삭제 테스트")
    @Transactional
    void deleteAllByHostUserTest() {
        // given
        Users host = users.get(0);
        Friend friend2 = friends.get(1);

        // when
        friendRepository.deleteAllByHostUser(host);

        List<Friend> actuals = friendRepository.findAll();

        assertAll(
                () -> assertThat(actuals).hasSize(1),
                () -> assertThat(actuals.get(0).getId()).isEqualTo(friend2.getId())
        );
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("host").email("host").point(1).age(1).kakaoId(1L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("user1").email("user1").point(1).age(1).kakaoId(2L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("user2").email("user2").point(1).age(1).kakaoId(3L).gender(Gender.F).imageUrl("image").role(Role.USER).build();
        return userRepository.saveAll(List.of(user1, user2, user3));
    }

    private List<Friend> createFriends() {
        Friend friend1 = Friend.builder().id(1L).hostUser(users.get(0)).friendUser(users.get(1)).build();
        Friend friend2 = Friend.builder().id(2L).hostUser(users.get(1)).friendUser(users.get(2)).build();
        return friendRepository.saveAll(List.of(friend1, friend2));
    }
}