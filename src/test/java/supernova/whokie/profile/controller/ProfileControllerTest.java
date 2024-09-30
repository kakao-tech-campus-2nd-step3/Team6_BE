package supernova.whokie.profile.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.infrastructure.ProfileRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.profiles.active=default",
    "jwt.secret=abcd"
})
public class ProfileControllerTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    private Users user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        user = Users.builder()
            .name("test")
            .email("test@gmail.com")
            .point(100)
            .age(25)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();

        userRepository.save(user);

        profile = Profile.builder()
            .users(user)
            .todayVisited(2)
            .totalVisited(12)
            .description("test")
            .backgroundImageUrl("test")
            .build();

        profileRepository.save(profile);
    }

    //@Test
    @DisplayName("프로필 조회")
    void getProfileInfo() throws Exception {
        mockMvc.perform(get("/api/profile/{user-id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("test"))
            .andExpect(jsonPath("$.description").value("test"))
            .andExpect(jsonPath("$.todayVisited").value(2))
            .andExpect(jsonPath("$.totalVisited").value(12))
            .andExpect(jsonPath("$.backgroundImageUrl").value("test"))
            .andDo(print());
    }

}
