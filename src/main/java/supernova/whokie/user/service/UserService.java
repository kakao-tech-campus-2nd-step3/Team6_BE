package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.controller.dto.KakaoAccount;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.repository.UserRepository;
import supernova.whokie.user.util.KakaoApiCaller;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoApiCaller kakaoApiCaller;

    public String getCodeUrl() {
        return kakaoApiCaller.createCodeUrl();
    }

    @Transactional
    public void register(String code) {
        KakaoAccount kakaoAccount = kakaoApiCaller.extractUserInfo(code);

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
    }
}
