package supernova.whokie.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.dto.UserCommand;
import supernova.whokie.user.service.dto.UserModel;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserReaderService userReaderService;

    @Mock
    UserWriterService userWriterService;

    @Mock
    JwtProvider jwtProvider;

    private Users user;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    @DisplayName("개인 정보 입력")
    void addPersonalInformationTest() {
        // given
        Users user1 = user;
        String jwt = "jwtjwtdsa";
        UserCommand.Info command = UserCommand.Info.builder().name("name").gender(Gender.F).birthDate(LocalDate.now()).build();
        Users updatedUser = Users.builder().id(user1.getId()).name(command.name()).email(user1.getEmail()).point(user.getPoint()).birthDate(command.birthDate()).kakaoId(user1.getKakaoId()).gender(command.gender()).imageUrl(user1.getImageUrl()).role(user1.getRole()).build();
        given(userWriterService.updateUserPersonalInfo(user1.getId(), command))
                .willReturn(updatedUser);
        given(jwtProvider.createToken(user1.getId(), updatedUser.getRole()))
                .willReturn(jwt);

        // when
        var actual = userService.addPersonalInformation(user1.getId(), command);

        // then
        assertAll(
                () -> assertThat(actual.jwt()).isEqualTo(jwt),
                () -> assertThat(actual.userId()).isEqualTo(user1.getId()),
                () -> assertThat(actual.role()).isEqualTo(user1.getRole())

        );
    }

    @Test
    @DisplayName("내 포인트 조회")
    void getPoint() {
        // given
        given(userReaderService.getUserById(user.getId())).willReturn(user);

        // when
        UserModel.Point point = userService.getPoint(user.getId());

        // then
        assertAll(
            () -> assertEquals(1000, point.amount()),
            () -> then(userReaderService).should().getUserById(user.getId())
        );
    }

    private Users createUser() {
        return user = Users.builder()
            .id(1L)
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .birthDate(LocalDate.now())
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();
    }
}
