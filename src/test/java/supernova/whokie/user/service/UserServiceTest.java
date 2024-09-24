package supernova.whokie.user.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.controller.dto.UserResponse.Point;
import supernova.whokie.user.repository.UserRepository;
import supernova.whokie.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("내 포인트 조회")
    void getPoint() {
        // given
        Users user = Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .age(30)
            .kakaoCode("code")
            .gender(Gender.M)
            .imageUrl("test")
            .role(Role.USER)
            .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        Point point = userService.getPoint(1L);

        // then
        assertEquals(1000, point.amount());
        then(userRepository).should().findById(1L);
    }

    @Test
    @DisplayName("내 정보 조회")
    void getUserInfo() throws Exception{
        // given
        Users user = new Users(1L, "test", "test@gmail.com", 1000, 20, "code", Gender.M, "test",
            Role.USER);

        // 리플렉션을 사용해 createdAt 수동 설정
        Field createdAtField = BaseTimeEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(user, LocalDateTime.now());

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserResponse.Info userInfo = userService.getUserInfo(1L);

        // then
        assertThat(userInfo.name()).isEqualTo(user.getName());
        assertThat(userInfo.email()).isEqualTo(user.getEmail());
        assertThat(userInfo.age()).isEqualTo(user.getAge());
        assertThat(userInfo.gender()).isEqualTo(user.getGender());
        assertThat(userInfo.role()).isEqualTo(user.getRole());

        then(userRepository).should().findById(1L);
    }
}
