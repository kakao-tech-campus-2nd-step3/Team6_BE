package supernova.whokie.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtInterceptor;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
            .name("test")
            .email("test@gmail.com")
            .point(100)
            .age(25)
            .kakaoId(1L)
            .gender(Gender.M)
            .imageUrl("imageUrl")
            .role(Role.USER)
            .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("유저 정보 조회")
    void getUserInfo() throws Exception {
        String token = jwtProvider.createToken(user.getId(), user.getRole());
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/api/user/mypage")
                .header("Authorization", "Bearer " + token)
                .requestAttr("userId", String.valueOf(user.getId()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@gmail.com"))
            .andExpect(jsonPath("$.gender").value("M"))
            .andExpect(jsonPath("$.age").value(25))
            .andExpect(jsonPath("$.name").value("test"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andDo(print());
    }

    //@Test
    @DisplayName("유저 포인트 조회")
    void getUserPoint() throws Exception {
        String token = jwtProvider.createToken(user.getId(), user.getRole());
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/api/user/point")
                .header("Authorization", "Bearer " + token)
                .requestAttr("userId", String.valueOf(user.getId()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(100))
            .andDo(print());
    }
}
