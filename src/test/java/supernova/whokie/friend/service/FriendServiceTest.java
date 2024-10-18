package supernova.whokie.friend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.friend.Friend;
import supernova.whokie.friend.infrastructure.apiCaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apiCaller.dto.KakaoDto;
import supernova.whokie.friend.infrastructure.repository.FriendRepository;
import supernova.whokie.friend.service.dto.FriendCommand;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
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
    private KakaoTokenService kakaoTokenService;
    @MockBean
    private JwtProvider jwtProvider;
    @PersistenceContext
    EntityManager entityManager;

    private List<KakaoDto.Profile> profiles;
    private List<Users> users;

    @BeforeEach
    void setUp() {
        profiles = createProfiles();
        users = createUsers();
    }

    @Test
    @DisplayName("getKakaoFriends 테스트")
    void getKakaoFriendsTest() {
        // given
        String accessToken = "accessToken";
        Users host = users.get(0);
        Users user1 = users.get(1);
        List<KakaoDto.Profile> profileList = List.of(profiles.get(1), profiles.get(2), profiles.get(3));
        KakaoDto.Friends kakaodto = new KakaoDto.Friends(null, profileList);
        given(kakaoTokenService.refreshIfAccessTokenExpired(any()))
            .willReturn(accessToken);
        given(apiCaller.getKakaoFriends(eq(accessToken)))
            .willReturn(kakaodto);
        friendRepository.save(new Friend(1L, host, user1));


        // when
        List<FriendModel.Info> actual = friendService.getKakaoFriends(host.getId());

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual.get(0).isFriend()).isTrue(),
                () -> assertThat(actual.get(1).isFriend()).isFalse(),
                () -> assertThat(actual.get(2).isFriend()).isFalse()
        );
    }

    @Test
    @DisplayName("새로운 친구 리스트 저장")
    void saveFriendsTest() {
        // given
        Users host = users.get(0);
        Users user1 = users.get(1);
        Users user2 = users.get(2);
        Users user3 = users.get(3);
        Long hostId = host.getId();

        FriendCommand.Update command = FriendCommand.Update.builder()
            .friendIds(List.of(user1.getId(), user2.getId(), user3.getId()))
            .build();

        // when
        friendService.saveFriends(hostId, command, new ArrayList<>());
        List<Friend> actual = friendRepository.findByHostUserIdFetchJoin(hostId);

        // then
        assertThat(actual).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("누락된 Friend 삭제")
    void deleteFriendsTest() {
        // given
        Users host = users.get(0);
        Users user1 = users.get(1);
        Users user2 = users.get(2);
        Users user3 = users.get(3);
        Long hostId = host.getId();
        Friend friend1 = new Friend(1L, host, user1);
        Friend friend2 = new Friend(2L, host, user2);
        Friend friend3 = new Friend(3L, host, user3);
        friendRepository.saveAll(List.of(friend1, friend2, friend3));

        FriendCommand.Update command = FriendCommand.Update.builder()
            .friendIds(List.of(user1.getId(), user2.getId()))
            .build();

        entityManager.flush();
        entityManager.clear();

        // when
        friendService.deleteFriends(command, List.of(friend1, friend2, friend3));
        List<Friend> actual = friendRepository.findByHostUserIdFetchJoin(hostId);

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("새로운 친구만 추출")
    void filterNewFriendsTest() {
        // given
        Users user1 = users.get(0);
        Users user2 = users.get(1);
        Users user3 = users.get(2);
        Users user4 = users.get(3);
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
        Users user1 = users.get(0);
        Users user2 = users.get(1);
        Users user3 = users.get(2);
        Users user4 = users.get(3);
        List<Users> usersList = List.of(user1, user2, user3);
        List<Long> friendUserIds = usersList.stream().map(Users::getId).toList();

        Friend friend1 = Friend.builder().friendUser(user3).build();
        Friend friend2 = Friend.builder().friendUser(user4).build();
        List<Friend> existingFriends = List.of(friend1, friend2);

        // when
        List<Long> actual = friendService.filteringDeleteFriendUserIds(friendUserIds,
            existingFriends);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst()).isEqualTo(friend2.getId());
    }


    private List<KakaoDto.Profile> createProfiles() {
        KakaoDto.Profile profile1 = new KakaoDto.Profile(1L, "uuid1", false, "nickname1", "image1");
        KakaoDto.Profile profile2 = new KakaoDto.Profile(2L, "uuid2", false, "nickname2", "image2");
        KakaoDto.Profile profile3 = new KakaoDto.Profile(3L, "uuid3", false, "nickname3", "image3");
        KakaoDto.Profile profile4 = new KakaoDto.Profile(4L, "uuid4", false, "nickname3", "image3");
        return List.of(profile1, profile2, profile3, profile4);
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("name").email("email1").point(0).age(1)
                .kakaoId(profiles.get(0).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("name").email("email2").point(0).age(1)
                .kakaoId(profiles.get(1).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("name").email("email3").point(0).age(1)
                .kakaoId(profiles.get(2).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user4 = Users.builder().id(4L).name("name").email("email4").point(0).age(1)
                .kakaoId(profiles.get(3).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        return userRepository.saveAll(List.of(user1, user2, user3, user4));
    }
}