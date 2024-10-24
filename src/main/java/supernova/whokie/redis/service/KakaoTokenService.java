package supernova.whokie.redis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.redis.entity.KakaoAccessToken;
import supernova.whokie.redis.entity.KakaoRefreshToken;
import supernova.whokie.redis.infrastructure.repository.AccessTokenRepository;
import supernova.whokie.redis.infrastructure.repository.RefreshTokenRepository;
import supernova.whokie.user.infrastructure.apicaller.UserApiCaller;
import supernova.whokie.user.infrastructure.apicaller.dto.RefreshedTokenInfoResponse;
import supernova.whokie.user.infrastructure.apicaller.dto.TokenInfoResponse;

@Service
@AllArgsConstructor
public class KakaoTokenService {
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserApiCaller userApiCaller;

    public void saveToken(Long userId, TokenInfoResponse kakaoTokenDto) {
        accessTokenRepository.save(new KakaoAccessToken(userId, kakaoTokenDto.accessToken(), kakaoTokenDto.expiresIn()));
        refreshTokenRepository.save(new KakaoRefreshToken(userId, kakaoTokenDto.refreshToken(), kakaoTokenDto.refreshTokenExpiresIn()));
    }

    public void deleteAccessToken(Long userId) {
        accessTokenRepository.deleteById(userId);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    public String refreshIfAccessTokenExpired(Long userId) {
        return accessTokenRepository.findById(userId)
                .orElseGet(() -> {
                    String refreshToken = refreshTokenRepository.findById(userId)
                            .orElseThrow(() -> new AuthenticationException("로그인이 만료되었습니다."))
                            .getRefreshToken();
                    RefreshedTokenInfoResponse tokenDto = userApiCaller.refreshAccessToken(refreshToken);
                    KakaoAccessToken newAccessToken = new KakaoAccessToken(userId, tokenDto.accessToken(), tokenDto.expiresIn());
                    accessTokenRepository.save(newAccessToken);
                    return newAccessToken;
                }).getAccessToken();
    }

}