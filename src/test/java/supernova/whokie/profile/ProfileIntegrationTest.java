package supernova.whokie.profile;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
@Import(EmbeddedRedisConfig.class)
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProfileVisitCountRepository profileVisitCountRepository;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedissonClient redissonClient;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        redissonClient.getKeys().flushall();
        user = createUser();
        profile = createProfile();
        ProfileVisitCount profileVisitCount = createProfileVisitCount();
    }

    @Test
    @DisplayName("프로필 조회")
    void getProfileInfo() throws Exception {
        String key = "keykey";
        given(s3Service.getSignedUrl(profile.getBackgroundImageUrl())).willReturn(key);

        mockMvc.perform(get("/api/profile/{user-id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("test"))
            .andExpect(jsonPath("$.description").value("test"))
            .andExpect(jsonPath("$.backgroundImageUrl").value(key))
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
            .imageUrl("url")
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