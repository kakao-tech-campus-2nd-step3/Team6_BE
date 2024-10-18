package supernova.whokie.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=default",
    "jwt.secret=abcd"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = createUser();
        profile = createProfile();
    }

    //@Test
    @DisplayName("프로필 조회")
    void getProfile() {
        // given
        given(profileRepository.findByUsersId(user.getId())).willReturn(Optional.of(profile));

        // when
        ProfileModel.Info result = profileService.getProfile(user.getId());

        // then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.name()).isEqualTo("test"),
            () -> assertThat(result.description()).isEqualTo("test"),
            () -> assertThat(result.backgroundImageUrl()).isEqualTo("test"),
            () -> then(profileRepository).should().findByUsersId(user.getId())
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
