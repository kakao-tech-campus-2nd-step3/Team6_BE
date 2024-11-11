package supernova.whokie.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.friend.infrastructure.apicaller.FriendKakaoApiCaller;
import supernova.whokie.friend.infrastructure.apicaller.dto.KakaoDto;
import supernova.whokie.friend.service.dto.FriendModel;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;
    @Mock
    private FriendKakaoApiCaller apiCaller;
    @Mock
    private KakaoTokenService kakaoTokenService;
    @Mock
    private FriendReaderService friendReaderService;
    @Mock
    private UserReaderService userReaderService;
  
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
        Users user2 = users.get(2);
        Users user3 = users.get(3);
        List<KakaoDto.Profile> profileList = List.of(profiles.get(1), profiles.get(2), profiles.get(3));
        KakaoDto.Friends kakaodto = new KakaoDto.Friends(null, profileList);
        given(kakaoTokenService.refreshIfAccessTokenExpired(any())).willReturn(accessToken);
        given(apiCaller.getKakaoFriends(eq(accessToken))).willReturn(kakaodto);
        List<Long> kakaoId = profileList.stream().map(KakaoDto.Profile::id).toList();
        given(userReaderService.getUserListByKakaoIdIn(eq(kakaoId))).willReturn(List.of(user1, user2, user3));
        given(friendReaderService.getFriendIdsByHostUser(eq(host.getId()))).willReturn(Set.of(user1.getId()));

        // when
        List<FriendModel.Info> actual = friendService.getKakaoFriends(host.getId());

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual.get(0).isFriend()).isTrue(),
                () -> assertThat(actual.get(1).isFriend()).isFalse(),
                () -> assertThat(actual.get(2).isFriend()).isFalse()
        );
    }

    private List<KakaoDto.Profile> createProfiles() {
        KakaoDto.Profile profile1 = new KakaoDto.Profile(1L, "uuid1", false, "nickname1", "image1");
        KakaoDto.Profile profile2 = new KakaoDto.Profile(2L, "uuid2", false, "nickname2", "image2");
        KakaoDto.Profile profile3 = new KakaoDto.Profile(3L, "uuid3", false, "nickname3", "image3");
        KakaoDto.Profile profile4 = new KakaoDto.Profile(4L, "uuid4", false, "nickname3", "image3");
        return List.of(profile1, profile2, profile3, profile4);
    }

    private List<Users> createUsers() {
        Users user1 = Users.builder().id(1L).name("name").email("email1").point(0).birthDate(LocalDate.now())
                .kakaoId(profiles.get(0).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user2 = Users.builder().id(2L).name("name").email("email2").point(0).birthDate(LocalDate.now())
                .kakaoId(profiles.get(1).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user3 = Users.builder().id(3L).name("name").email("email3").point(0).birthDate(LocalDate.now())
                .kakaoId(profiles.get(2).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        Users user4 = Users.builder().id(4L).name("name").email("email4").point(0).birthDate(LocalDate.now())
                .kakaoId(profiles.get(3).id()).gender(Gender.F).imageUrl("sfd").role(Role.USER).build();
        return List.of(user1, user2, user3, user4);
    }
}