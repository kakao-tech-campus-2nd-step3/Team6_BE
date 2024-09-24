package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.apiCaller.dto.KakaoAccount;
import supernova.whokie.user.controller.dto.UserResponse;
import supernova.whokie.user.infrastructure.apiCaller.dto.Partner;
import supernova.whokie.user.infrastructure.apiCaller.dto.UserInfoResponse;
import supernova.whokie.user.repository.UserRepository;
import supernova.whokie.user.infrastructure.apiCaller.UserApiCaller;
import supernova.whokie.user.service.dto.UserModel;

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
        UserInfoResponse userInfoResponse = userApiCaller.extractUserInfo(code);
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();
        Partner partner = userInfoResponse.forPartner();

        Users user = userRepository.findByEmail(kakaoAccount.email())
            .orElseGet(() -> userRepository.save(
                Users.builder()
                    .name(kakaoAccount.name())
                    .email(kakaoAccount.email())
                    .point(0)
                    .age(LocalDate.now().getYear() - Integer.parseInt(kakaoAccount.birthYear()))
                    .gender(Gender.fromString(kakaoAccount.gender()))
                    .imageUrl(kakaoAccount.profile().profileImageUrl())
                    .role(Role.USER)
                    //.uuid(partner.uuid())
                    .build()
            ));

        String token = jwtProvider.createToken(user.getId(), user.getRole());
        System.out.println(token);
        return token;
    }

    public UserModel.Info getUserInfo(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return UserModel.Info.from(user);
    }

    public UserModel.Point getPoint(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return UserModel.Point.from(user);
    }
}
