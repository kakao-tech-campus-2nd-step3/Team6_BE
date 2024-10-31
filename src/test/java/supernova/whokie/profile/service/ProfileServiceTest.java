package supernova.whokie.profile.service;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.service.RedisVisitService;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileServiceTest {

    @Mock
    private RedisVisitService redisVisitService;

    @Mock
    private ProfileReaderService profileReaderService;

    @InjectMocks
    private ProfileService profileService;

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
        RedisVisitCount visitCount = RedisVisitCount.builder().hostId(user.getId()).dailyVisited(10).totalVisited(100).build();
        given(profileReaderService.getByUserId(user.getId())).willReturn(profile);
        given(redisVisitService.visitProfile(user.getId(), visitorIp)).willReturn(visitCount);

        // when
        ProfileModel.Info result = profileService.getProfile(user.getId(), visitorIp);

        // then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.name()).isEqualTo("test"),
            () -> assertThat(result.description()).isEqualTo("test"),
            () -> assertThat(result.backgroundImageUrl()).isEqualTo("test"),
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
