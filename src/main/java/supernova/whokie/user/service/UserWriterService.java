package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.apicaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apicaller.dto.UserInfoResponse;
import supernova.whokie.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserWriterService {

    private final UserRepository userRepository;

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void save(Users user) {
        userRepository.save(user);
    }

    @Transactional
    public Users saveUserFromKakao(UserInfoResponse userInfoResponse) {
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();
        Users user = userRepository.save(
            Users.builder()
                .name(kakaoAccount.name())
                .email(kakaoAccount.email())
                .point(0)
                .age(LocalDate.now().getYear() - Integer.parseInt(kakaoAccount.birthYear()))
                .gender(Gender.fromString(kakaoAccount.gender()))
                .imageUrl(kakaoAccount.profile().profileImageUrl())
                .role(Role.USER)
                .kakaoId(userInfoResponse.id())
                .build());
        return user;
    }


}
