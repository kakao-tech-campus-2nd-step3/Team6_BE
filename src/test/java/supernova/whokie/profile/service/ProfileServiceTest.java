package supernova.whokie.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.infrastructure.ProfileRepository;
import supernova.whokie.profile.service.dto.ProfileModel;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(100)
            .age(25)
            .kakaoCode("kakao_code")
            .gender(Gender.M)
            .role(Role.USER)
            .build();

        profile = Profile.builder()
            .id(1L)
            .users(user)
            .todayVisited(2)
            .totalVisited(12)
            .description("test")
            .backgroundImageUrl("test")
            .build();
    }

    @Test
    @DisplayName("프로필 조회")
    void getProfile() {
        // given
        given(profileRepository.findByUsersId(user.getId())).willReturn(Optional.of(profile));

        // when
        ProfileModel.Info result = profileService.getProfile(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("test");
        assertThat(result.description()).isEqualTo("test");
        assertThat(result.todayVisited()).isEqualTo(2);
        assertThat(result.totalVisited()).isEqualTo(12);
        assertThat(result.backgroundImageUrl()).isEqualTo("test");

        then(profileRepository).should().findByUsersId(user.getId());
    }
}
