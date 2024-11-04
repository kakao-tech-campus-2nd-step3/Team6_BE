package supernova.whokie.profile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.service.RedisVisitService;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private RedisVisitService redisVisitService;

    @Mock
    private ProfileReaderService profileReaderService;

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private S3Service s3Service;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = createUser();
        profile = createProfile();
    }

    @Test
    @DisplayName("프로필 조회")
    void getProfileTest() {
        // given
        String visitorIp = "visitorIp";
        String key = "keykey";
        RedisVisitCount visitCount = RedisVisitCount.builder().hostId(user.getId()).dailyVisited(10).totalVisited(100).build();
        given(profileReaderService.getByUserId(user.getId())).willReturn(profile);
        given(redisVisitService.visitProfile(user.getId(), visitorIp)).willReturn(visitCount);
        given(s3Service.getSignedUrl(profile.getBackgroundImageUrl())).willReturn(key);


        // when
        ProfileModel.Info result = profileService.getProfile(user.getId(), visitorIp);

        // then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.name()).isEqualTo("test"),
            () -> assertThat(result.description()).isEqualTo("test"),
            () -> assertThat(result.backgroundImageUrl()).isEqualTo(key),
            () -> assertThat(result.todayVisited()).isEqualTo(visitCount.getDailyVisited()),
            () -> assertThat(result.totalVisited()).isEqualTo(visitCount.getTotalVisited()),
            () -> then(profileReaderService).should().getByUserId(user.getId())
        );
    }

    private Users createUser() {
        return Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .imageUrl("url")
            .build();
    }

    private Profile createProfile() {
        return profile = Profile.builder()
            .users(user)
            .description("test")
            .backgroundImageUrl("test")
            .build();
    }
}
