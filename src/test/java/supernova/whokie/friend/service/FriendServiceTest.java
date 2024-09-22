package supernova.whokie.friend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.repository.FriendRepository;
import supernova.whokie.friend.service.dto.KakaoDto;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FriendServiceTest {
    @Autowired
    private FriendService friendService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FriendRepository friendRepository;
    @MockBean
    private FriendKakaoApiCaller apiCaller;

    @Test
    @DisplayName("새로 저장할 Users만 추출")
    void filterNewProfileTest() {
        // given
        KakaoDto.Profile profile1 = new KakaoDto.Profile(1L, "string1", null, null, null);
        KakaoDto.Profile profile2 = new KakaoDto.Profile(2L, "string2", null, null, null);
        List<KakaoDto.Profile> profiles = List.of(profile1, profile2);
        Users user1 = Users.builder().id(2L).kakaoCode("string2").build();
        Users user2 = Users.builder().id(3L).kakaoCode("string3").build();
        List<Users> users = List.of(user1, user2);

        // when
        List<Users> actual = friendService.filterNewProfiles(profiles, users);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getKakaoCode()).isEqualTo(profile1.uuid());
    }


    @Test
    @DisplayName("새로운 친구만 추출")
    void filterNewFriendsTest() {
        // given
        Users host = Users.builder().id(1L).name("host").build();
        Users user1 = Users.builder().id(2L).name("user1").build();
        Users user2 = Users.builder().id(3L).name("user2").build();
        Users user3 = Users.builder().id(4L).name("user3").build();
        Users user4 = Users.builder().id(5L).name("user4").build();
        List<Users> users = List.of(user1, user2, user3);

        Friend friend1 = Friend.builder().friendUser(user3).build();
        Friend friend2 = Friend.builder().friendUser(user4).build();
        List<Friend> friends = List.of(friend1, friend2);

        // when
        List<Friend> actual = friendService.filterNewFriends(users, friends, host);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getFriendUser()).isEqualTo(user1);
        assertThat(actual.get(1).getFriendUser()).isEqualTo(user2);
    }
}