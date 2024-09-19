package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.KakaoAccount;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.repository.UserRepository;
import supernova.whokie.user.util.UserApiCaller;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserApiCaller userApiCaller;

    public String getCodeUrl() {
        return userApiCaller.createCodeUrl();
    }

    @Transactional
    public String register(String code) {
        KakaoAccount kakaoAccount = userApiCaller.extractUserInfo(code);

        Users user = userRepository.findByEmail(kakaoAccount.email())
            .orElseGet(() -> userRepository.save(
                Users.builder()
                    .name(kakaoAccount.name())
                    .email(kakaoAccount.email())
                    .point(0)
                    .age(LocalDate.now().getYear() - Integer.parseInt(kakaoAccount.birthyear()))
                    .gender(Gender.fromString(kakaoAccount.gender()))
                    .imageUrl(kakaoAccount.profile().profile_image_url())
                    .role(Role.USER)
                    .build()
            ));

        String token = jwtProvider.createToken(user.getId(), user.getRole());
        System.out.println(token);
        return token;
    }

    public UserResponse.Info getUserInfo(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow();

        return UserResponse.Info.from(user);
    }
}
