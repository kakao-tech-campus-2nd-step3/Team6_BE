package supernova.whokie.redis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.redis.entity.KakaoAccessToken;
import supernova.whokie.redis.entity.KakaoRefreshToken;
import supernova.whokie.redis.repository.AccessTokenRepository;
import supernova.whokie.redis.repository.RefreshTokenRepository;
import supernova.whokie.user.infrastructure.apiCaller.UserApiCaller;
import supernova.whokie.user.infrastructure.apiCaller.dto.TokenInfoResponse;

@Service
@AllArgsConstructor
public class KakaoTokenService {
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserApiCaller userApiCaller;

    public void saveToken(Long memberId, TokenInfoResponse kakaoTokenDto) {
        accessTokenRepository.save(new KakaoAccessToken(memberId, kakaoTokenDto.accessToken(), kakaoTokenDto.expiresIn()));
        refreshTokenRepository.save(new KakaoRefreshToken(memberId, kakaoTokenDto.refreshToken(), kakaoTokenDto.refreshTokenExpiresIn()));
    }

    public void deleteAccessToken(Long memberId) {
        accessTokenRepository.deleteById(memberId);
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    public String refreshIfAccessTokenExpired(Long memberId) {
        return accessTokenRepository.findById(memberId)
                .orElseGet(() -> {
                    String refreshToken = refreshTokenRepository.findById(memberId)
                            .orElseThrow(() -> new AuthenticationException("로그인이 만료되었습니다."))
                            .getRefreshToken();
                    TokenInfoResponse tokenDto = userApiCaller.refreshAccessToken(refreshToken);
                    return new KakaoAccessToken(memberId, tokenDto.accessToken(), tokenDto.expiresIn());
                }).getAccessToken();
    }

}