package supernova.whokie.user;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.user.controller.dto.UserResponse.Point;
import supernova.whokie.user.repository.UserRepository;
import supernova.whokie.user.service.UserService;

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
        Users user = new Users(1L, "test", "test@gmail.com", 1000, 20, "code", Gender.M, "test",
            Role.USER);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        Point point = userService.getPoint(1L);

        assertEquals(1000, point.amount());
        then(userRepository).should().findById(1L);
    }
}
