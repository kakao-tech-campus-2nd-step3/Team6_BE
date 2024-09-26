package supernova.whokie.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import supernova.whokie.global.auth.JwtInterceptor;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.service.UserService;
import supernova.whokie.user.service.dto.UserModel;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @Test
    @DisplayName("유저 정보 조회")
    void getUserInfo_success() throws Exception {
        Long userId = 1L;
        UserModel.Info userInfo = UserModel.Info.builder()
            .email("test@gmail.com")
            .gender(Gender.M)
            .age(25)
            .name("TestUser")
            .role(Role.USER)
            .build();

        given(userService.getUserInfo(userId)).willReturn(userInfo);

        String token = jwtProvider.createToken(userId, userInfo.role());
        given(jwtInterceptor.preHandle(any(), any(), any())).willReturn(true);

        mockMvc.perform(get("/api/user/mypage")
                .header("Authorization", "Bearer " + token)
                .requestAttr("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@gmail.com"))
            .andExpect(jsonPath("$.gender").value("M"))
            .andExpect(jsonPath("$.age").value(25))
            .andExpect(jsonPath("$.name").value("TestUser"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andDo(print());
    }

    @Test
    @DisplayName("유저 포인트 조회")
    void getUserPoint_success() throws Exception {
        Long userId = 1L;
        UserModel.Point userPoint = UserModel.Point.builder()
            .amount(100)
            .build();

        given(userService.getPoint(userId)).willReturn(userPoint);

        mockMvc.perform(get("/api/user/point")
                .requestAttr("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(100))
            .andDo(print());
    }
}
