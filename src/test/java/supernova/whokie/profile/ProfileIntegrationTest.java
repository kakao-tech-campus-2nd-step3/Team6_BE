package supernova.whokie.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class)
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProfileVisitCountRepository profileVisitCountRepository;

    @Autowired
    private MockMvc mockMvc;

    private Users user;
    private Profile profile;
    private ProfileVisitCount profileVisitCount;

    @BeforeEach
    void setUp() {
        user = createUser();
        profile = createProfile();
        profileVisitCount = createProfileVisitCount();
    }

    @Test
    @DisplayName("프로필 조회")
    void getProfileInfo() throws Exception {
        mockMvc.perform(get("/api/profile/{user-id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("test"))
            .andExpect(jsonPath("$.description").value("test"))
            .andExpect(jsonPath("$.backgroundImageUrl").value("test"))
            .andDo(print());
    }

    private Users createUser() {
        Users user = Users.builder()
            .name("test")
            .email("test@gmail.com")
            .point(1500)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();

        return userRepository.save(user);
    }

    private Profile createProfile() {
        Profile profile = Profile.builder()
            .users(user)
            .description("test")
            .backgroundImageUrl("test")
            .build();

        return profileRepository.save(profile);
    }

    private ProfileVisitCount createProfileVisitCount() {
        ProfileVisitCount visitCount = ProfileVisitCount.builder()
                .hostId(user.getId())
                .dailyVisited(0)
                .totalVisited(0)
                .build();
        return profileVisitCountRepository.save(visitCount);
    }
}