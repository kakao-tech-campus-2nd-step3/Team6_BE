package supernova.whokie.user.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.profile.Profile;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.infrastructure.repository.ProfileRepository;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.apicaller.UserApiCaller;
import supernova.whokie.user.infrastructure.apicaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apicaller.dto.TokenInfoResponse;
import supernova.whokie.user.infrastructure.apicaller.dto.UserInfoResponse;
import supernova.whokie.user.infrastructure.repository.UserRepository;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileVisitCountRepository profileVisitCountRepository;
    private final JwtProvider jwtProvider;
    private final UserApiCaller userApiCaller;
    private final UserReaderService userReaderService;
    private final KakaoTokenService kakaoTokenService;

    public String getCodeUrl() {
        return userApiCaller.createCodeUrl();
    }

    //TODO 리팩 필요
    @Transactional
    public String register(String code) {
        // 토큰 발급
        TokenInfoResponse tokenResponse = userApiCaller.getAccessToken(code);
        String accessToken = tokenResponse.accessToken();
        System.out.println(tokenResponse.refreshToken());
        // 카카오 사용자 정보 요청
        UserInfoResponse userInfoResponse = userApiCaller.extractUserInfo(accessToken);
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();

        // Users 저장 및 중복 체크
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
                            .backgroundImageUrl(kakaoAccount.profile().profileImageUrl())
                            .build();

                    profileRepository.save(profile);

                    ProfileVisitCount visitCount = ProfileVisitCount.builder()
                            .hostId(newUser.getId())
                            .dailyVisited(0)
                            .totalVisited(0)
                            .build();
                    profileVisitCountRepository.save(visitCount);

                    return newUser;
                });

        // kakao token 저장
        kakaoTokenService.saveToken(user.getId(), tokenResponse);

        return jwtProvider.createToken(user.getId(), user.getRole());
    }

    public UserModel.Info getUserInfo(Long userId) {
        Users user = userReaderService.getUserById(userId);
        return UserModel.Info.from(user);
    }

    public UserModel.Point getPoint(Long userId) {
        Users user = userReaderService.getUserById(userId);
        return UserModel.Point.from(user);
    }
}
