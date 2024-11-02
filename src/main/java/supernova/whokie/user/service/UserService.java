package supernova.whokie.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.profile.infrastructure.downloader.ImageDownloader;
import supernova.whokie.profile.service.ProfileVisitWriterService;
import supernova.whokie.profile.service.ProfileWriterService;
import supernova.whokie.redis.service.KakaoTokenService;
import supernova.whokie.s3.event.S3EventDto;
import supernova.whokie.s3.service.S3Service;
import supernova.whokie.user.Users;
import supernova.whokie.user.event.UserEventDto;
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
    private final ImageDownloader imageDownloader;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;

    public String getCodeUrl() {
        return userApiCaller.createCodeUrl();
    }

    //TODO 리팩 필요
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

                // 프로필 이미지 다운로드 및 업로드
                var event = UserEventDto.UploadImage.toDto(
                        kakaoAccount.profile().profileImageUrl(), Constants.USER_IMAGE_FOLRDER, newUser.getId());
                eventPublisher.publishEvent(event);

                return newUser;
            });

        // kakao token 저장
        kakaoTokenService.saveToken(user.getId(), tokenResponse);
        String jwt = jwtProvider.createToken(user.getId(), user.getRole());
        return UserModel.Login.from(jwt, user.getId());
    }

    public UserModel.Point getPoint(Long userId) {
        Users user = userReaderService.getUserById(userId);
        return UserModel.Point.from(user);
    }

    public MultipartFile downloadImageFile(String url) {
        try {
            return imageDownloader.downloadImageAsMultipartFile(url);
        } catch (Exception e) {
            throw new RuntimeException(MessageConstants.PROFILE_IMAGE_ERROR_MESSAGE);
        }
    }

    @Transactional
    public void uploadImageUrl(Long userId, MultipartFile imageFile, String type) {
        String key = s3Service.createKey(Constants.USER_IMAGE_FOLRDER, userId, imageFile, type);
        S3EventDto.Upload event = S3EventDto.Upload.toDto(imageFile, key);
        eventPublisher.publishEvent(event);

        updateImageUrl(userId, key);
    }

    @Transactional
    public void updateImageUrl(Long userId, String imageUrl) {
        Users user = userReaderService.getUserById(userId);
        user.updateImageUrl(imageUrl);
    }
}
