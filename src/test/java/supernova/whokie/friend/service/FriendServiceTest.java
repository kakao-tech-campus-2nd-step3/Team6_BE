package supernova.whokie.friend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.apiCaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apiCaller.dto.KakaoDto;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FriendServiceTest {
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @MockBean
    private FriendKakaoApiCaller apiCaller;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("getKakaoFriends 테스트")
    void getKakaoFriendsTest() {
        // given
        Long userId = 1L;
        KakaoDto.Profile profile1 = new KakaoDto.Profile(2L, "uuid1", false, "nickname1", "image1");
        KakaoDto.Profile profile2 = new KakaoDto.Profile(3L, "uuid2", false, "nickname2", "image2");
        KakaoDto.Profile profile3 = new KakaoDto.Profile(4L, "uuid3", false, "nickname3", "image3");
        List<KakaoDto.Profile> profiles = List.of(profile1, profile2, profile3);
        KakaoDto.Friends kakaodto = new KakaoDto.Friends(null, profiles);
        given(apiCaller.getKakaoFriends(any()))
                .willReturn(kakaodto);

        Users host = Users.builder().id(userId).name("name").email("email1").point(0).age(1).kakaoId(1L).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user1 = Users.builder().id(2L).name("name").email("email2").point(0).age(1).kakaoId(profile1.id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user2 = Users.builder().id(3L).name("name").email("email3").point(0).age(1).kakaoId(profile2.id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user3 = Users.builder().id(4L).name("name").email("email4").point(0).age(1).kakaoId(profile3.id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        userRepository.saveAll(List.of(host, user1, user2, user3));

        Friend friend = new Friend(1L, host, user3);
        friendRepository.save(friend);

        // when
        List<FriendModel.Info> actual = friendService.getKakaoFriends(userId);

        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).isFriend()).isFalse();
        assertThat(actual.get(1).isFriend()).isFalse();
        assertThat(actual.get(2).isFriend()).isTrue();
    }


    @Test
    @DisplayName("새로운 친구만 추출")
    void filterNewFriendsTest() {
        // given
        Users user1 = Users.builder().id(2L).name("user1").build();
        Users user2 = Users.builder().id(3L).name("user2").build();
        Users user3 = Users.builder().id(4L).name("user3").build();
        Users user4 = Users.builder().id(5L).name("user4").build();
        List<Users> users = List.of(user1, user2, user3);
        List<Long> userIds = users.stream().map(Users::getId).toList();

        Friend friend1 = Friend.builder().friendUser(user3).build();
        Friend friend2 = Friend.builder().friendUser(user4).build();
        List<Friend> friends = List.of(friend1, friend2);

        // when
        List<Long> actual = friendService.filteringNewFriendUserIds(userIds, friends);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEqualTo(user1.getId());
        assertThat(actual.get(1)).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("삭제할 친구만 추출")
    void filterDeleteFriendsTest() {
        // given
        Users user1 = Users.builder().id(2L).name("user1").build();
        Users user2 = Users.builder().id(3L).name("user2").build();
        Users user3 = Users.builder().id(4L).name("user3").build();
        Users user4 = Users.builder().id(5L).name("user4").build();
        List<Users> users = List.of(user1, user2, user3);
        List<Long> friendUserIds = users.stream().map(Users::getId).toList();

        Friend friend1 = Friend.builder().friendUser(user3).build();
        Friend friend2 = Friend.builder().friendUser(user4).build();
        List<Friend> existingFriends = List.of(friend1, friend2);

        // when
        List<Friend> actual = friendService.filteringDeleteFriendUserIds(friendUserIds, existingFriends);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst()).isEqualTo(friend2);
    }

    @Test
    @DisplayName("FriendUserId들을 Set으로 추출")
    void extractFriendUserIdAsSetTest() {
        // given
        Users user1 = Users.builder().id(1L).name("user1").build();
        Users user2 = Users.builder().id(2L).name("user2").build();
        Users user3 = Users.builder().id(3L).name("user3").build();

        Friend friend1 = Friend.builder().friendUser(user1).build();
        Friend friend2 = Friend.builder().friendUser(user2).build();
        List<Friend> existingFriends = List.of(friend1, friend2);

        // when
        Set<Long> actual = friendService.extractFriendUserIdAsSet(existingFriends);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.contains(user1.getId())).isTrue();
        assertThat(actual.contains(user2.getId())).isTrue();
        assertThat(actual.contains(user3.getId())).isFalse();
    }
}