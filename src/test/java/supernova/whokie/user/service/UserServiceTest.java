package supernova.whokie.user.service;

import org.junit.jupiter.api.BeforeEach;
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
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .point(1000)
                .age(30)
                .kakaoId(1L)
                .gender(Gender.M)
                .imageUrl("test")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("내 포인트 조회")
    void getPoint() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserModel.Point point = userService.getPoint(1L);

        // then
        assertEquals(1000, point.amount());
        then(userRepository).should().findById(1L);
    }

    @Test
    @DisplayName("내 정보 조회")
    void getUserInfo() throws Exception {
        // given
        // 리플렉션을 사용해 createdAt 수동 설정
        Field createdAtField = BaseTimeEntity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(user, LocalDateTime.now());

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserModel.Info userInfo = userService.getUserInfo(1L);

        // then
        assertThat(userInfo.name()).isEqualTo(user.getName());
        assertThat(userInfo.email()).isEqualTo(user.getEmail());
        assertThat(userInfo.age()).isEqualTo(user.getAge());
        assertThat(userInfo.gender()).isEqualTo(user.getGender());
        assertThat(userInfo.role()).isEqualTo(user.getRole());

        then(userRepository).should().findById(1L);
    }
}
