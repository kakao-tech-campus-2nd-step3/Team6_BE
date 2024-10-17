package supernova.whokie.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    @DisplayName("내 포인트 조회")
    void getPoint() {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        UserModel.Point point = userService.getPoint(user.getId());

        // then
        assertAll(
            () -> assertEquals(1000, point.amount()),
            () -> then(userRepository).should().findById(user.getId())
        );
    }

    @Test
    @DisplayName("내 정보 조회")
    void getUserInfo() throws Exception{
        // given
        Field createdAtField = BaseTimeEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(user, LocalDateTime.now());

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserModel.Info userInfo = userService.getUserInfo(user.getId());

        // then
        assertAll(
            () -> assertThat(userInfo.name()).isEqualTo(user.getName()),
            () -> assertThat(userInfo.email()).isEqualTo(user.getEmail()),
            () -> assertThat(userInfo.age()).isEqualTo(user.getAge()),
            () -> assertThat(userInfo.gender()).isEqualTo(user.getGender()),
            () -> assertThat(userInfo.role()).isEqualTo(user.getRole()),
            () -> then(userRepository).should().findById(user.getId())
        );
    }

    private Users createUser() {
        return user = Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .age(22)
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();
    }
}
