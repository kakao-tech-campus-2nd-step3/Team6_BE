package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.infrastructure.ProfileRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.apiCaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apiCaller.dto.UserInfoResponse;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.infrastructure.apiCaller.UserApiCaller;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JwtProvider jwtProvider;
    private final UserApiCaller userApiCaller;

    public String getCodeUrl() {
        return userApiCaller.createCodeUrl();
    }

    @Transactional
    public String register(String code) {
        UserInfoResponse userInfoResponse = userApiCaller.extractUserInfo(code);
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();

        Users user = userRepository.findByEmail(kakaoAccount.email())
            .orElseGet(() -> {
                Users newUser = userRepository.save(
                    Users.builder()
                        .name(kakaoAccount.name())
                        .email(kakaoAccount.email())
                        .point(0)
                        .age(LocalDate.now().getYear() - Integer.parseInt(kakaoAccount.birthYear()))
                        .gender(Gender.fromString(kakaoAccount.gender()))
                        .imageUrl(kakaoAccount.profile().profileImageUrl())
                        .role(Role.USER)
                        .kakaoId(userInfoResponse.id())
                        .build()
                );

                Profile profile = Profile.builder()
                    .users(newUser)
                    .todayVisited(0)
                    .totalVisited(0)
                    .backgroundImageUrl(kakaoAccount.profile().profileImageUrl())
                    .build();

                profileRepository.save(profile);
                return newUser;
            });

        String token = jwtProvider.createToken(user.getId(), user.getRole());
        return token;
    }

    public UserModel.Info getUserInfo(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        return UserModel.Info.from(user);
    }

    public UserModel.Point getPoint(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));

        return UserModel.Point.from(user);
    }

    @Transactional
    public String testRegister() {  // 로그인 테스트용
        Users user = Users.builder()
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

        userRepository.save(user);

        Profile profile = Profile.builder()
            .id(1L)
            .users(user)
            .todayVisited(2)
            .totalVisited(12)
            .description("test")
            .backgroundImageUrl("test")
            .build();

        profileRepository.save(profile);

        String token = jwtProvider.createToken(user.getId(), user.getRole());
        return token;
    }
}
