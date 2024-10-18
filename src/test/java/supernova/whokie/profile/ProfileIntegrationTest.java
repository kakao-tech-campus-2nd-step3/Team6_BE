package supernova.whokie.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
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
    private MockMvc mockMvc;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = createUser();
        profile = createProfile();
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

        userRepository.save(user);
        return user;
    }

    private Profile createProfile() {
        Profile profile = Profile.builder()
            .users(user)
            .description("test")
            .backgroundImageUrl("test")
            .build();

        profileRepository.save(profile);
        return profile;
    }
}