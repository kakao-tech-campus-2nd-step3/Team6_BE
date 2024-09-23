package supernova.whokie.friend.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FriendRepositoryTest {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("host로 모든 Friend 조회")
    void findByHostUserTest() {
        // given
        Users host = Users.builder().id(1L).name("host").build();
        Users user1 = Users.builder().id(2L).name("user1").build();
        Users user2 = Users.builder().id(3L).name("user2").build();
        userRepository.saveAll(List.of(host, user1, user2));
        Friend friend1 = Friend.builder().id(1L).hostUser(host).friendUser(user1).build();
        Friend friend2 = Friend.builder().id(2L).hostUser(user1).friendUser(user2).build();
        friendRepository.saveAll(List.of(friend1, friend2));

        // when
        List<Friend> actual = friendRepository.findByHostUser(host);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(friend1.getId());
        assertThat(actual.getFirst().getFriendUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("hostId로 모든 Friend 조회")
    void findByHostUserIdTest() {
        // given
        Users host = Users.builder().id(1L).name("host").build();
        Users user1 = Users.builder().id(2L).name("user1").build();
        Users user2 = Users.builder().id(3L).name("user2").build();
        userRepository.saveAll(List.of(host, user1, user2));
        Friend friend1 = Friend.builder().id(1L).hostUser(host).friendUser(user1).build();
        Friend friend2 = Friend.builder().id(2L).hostUser(user1).friendUser(user2).build();
        friendRepository.saveAll(List.of(friend1, friend2));

        // when
        List<Friend> actual = friendRepository.findByHostUserId(host.getId());

        // then

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(friend1.getId());
        assertThat(actual.getFirst().getFriendUser().getId()).isEqualTo(user1.getId());
    }
}