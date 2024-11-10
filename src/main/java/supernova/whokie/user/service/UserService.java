package supernova.whokie.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.profile.service.ProfileVisitWriterService;
import supernova.whokie.profile.service.ProfileWriterService;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.util.S3Util;
import supernova.whokie.user.Users;
import supernova.whokie.user.constants.UserConstants;
import supernova.whokie.user.infrastructure.apicaller.UserApiCaller;
import supernova.whokie.user.infrastructure.apicaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apicaller.dto.TokenInfoResponse;
import supernova.whokie.user.infrastructure.apicaller.dto.UserInfoResponse;
import supernova.whokie.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileWriterService profileWriterService;
    private final ProfileVisitWriterService profileVisitWriterService;
    private final JwtProvider jwtProvider;
    private final UserApiCaller userApiCaller;
    private final UserWriterService userWriterService;
    private final UserReaderService userReaderService;
    private final KakaoTokenService kakaoTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public String getCodeUrl() {
        return userApiCaller.createCodeUrl();
    }

    @Transactional
    public UserModel.Login register(String code) {
        // 토큰 발급
        TokenInfoResponse tokenResponse = userApiCaller.getAccessToken(code);
        String accessToken = tokenResponse.accessToken();
        // 카카오 사용자 정보 요청
        UserInfoResponse userInfoResponse = userApiCaller.extractUserInfo(accessToken);
        KakaoAccount kakaoAccount = userInfoResponse.kakaoAccount();

        // Users 저장 및 중복 체크
        Users user = userReaderService.findByEmail(kakaoAccount.email())
            .orElseGet(() -> {
                Users newUser = userWriterService.saveUserFromKakao(userInfoResponse);
                profileWriterService.saveFromKaKao(newUser);
                profileVisitWriterService.save(newUser.getId());

                return newUser;
            });

        // kakao token 저장
        kakaoTokenService.saveToken(user.getId(), tokenResponse);
        String jwt = jwtProvider.createToken(user.getId(), user.getRole());
        return UserModel.Login.from(jwt, user.getId());
    }

    @Transactional(readOnly = true)
    public UserModel.Point getPoint(Long userId) {
        Users user = userReaderService.getUserById(userId);
        return UserModel.Point.from(user);
    }

    @Transactional
    public void uploadImageUrl(Long userId, MultipartFile imageFile) {
        String key = S3Util.generateS3Key(UserConstants.USER_IMAGE_FOLRDER, userId);
        S3EventDto.Upload event = S3EventDto.Upload.toDto(imageFile, key, UserConstants.USER_IMAGE_WIDTH, UserConstants.USER_IMAGE_HEIGHT);
        eventPublisher.publishEvent(event);

        updateImageUrl(userId, key);
    }

    @Transactional
    public void updateImageUrl(Long userId, String imageUrl) {
        Users user = userReaderService.getUserById(userId);
        user.updateImageUrl(imageUrl);
    }

    @Transactional(readOnly = true)
    public Page<UserModel.Info> getAllUsersPaging(Pageable pageable) {
        Page<Users> entities = userReaderService.getAllUsersPaging(pageable);
        return entities.map(UserModel.Info::from);
    }

    @Transactional(readOnly = true)
    public Page<UserModel.Info> searchUsers(String keyword, Pageable pageable) {
        Page<Users> entities = userReaderService.findByNameContainingOrEmailContaining(keyword, keyword, pageable);
        return entities.map(UserModel.Info::from);
    }
}
