package supernova.whokie.redis.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.global.auth.JwtProvider;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.redis.entity.KakaoAccessToken;
import supernova.whokie.redis.entity.KakaoRefreshToken;
import supernova.whokie.redis.repository.AccessTokenRepository;
import supernova.whokie.redis.repository.RefreshTokenRepository;
import supernova.whokie.user.infrastructure.apiCaller.UserApiCaller;
import supernova.whokie.user.infrastructure.apiCaller.dto.RefreshedTokenInfoResponse;
import supernova.whokie.user.infrastructure.apiCaller.dto.TokenInfoResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@DataRedisTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = KakaoTokenService.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(EmbeddedRedisConfig.class)
@TestPropertySource(properties = {
        "spring.profiles.active=default",
        "jwt.secret=abcd"
})
class KakaoTokenServiceTest {
    @Autowired
    private KakaoTokenService kakaoTokenService;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private UserApiCaller userApiCaller;

    @Test
    @DisplayName("카카오 토큰 저장 테스트")
    void saveTokenTest() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        Long userId = 1L;
        TokenInfoResponse tokenInfoResponse = new TokenInfoResponse(accessToken, "tokenType", refreshToken, 100L, 100L);

        // when
        kakaoTokenService.saveToken(userId, tokenInfoResponse);
        KakaoAccessToken accessEntity = accessTokenRepository.findById(1L).get();
        KakaoRefreshToken refreshEntity = refreshTokenRepository.findById(1L).get();

        // then
        assertThat(accessEntity.getAccessToken()).isEqualTo(accessToken);
        assertThat(refreshEntity.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("카카오 로그인 만료 테스트[실패]")
    void refreshIfAccessTokenExpiredTestFail() {
        // given
        Long userId = 1L;

        // when
        // then
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> kakaoTokenService.refreshIfAccessTokenExpired(userId));
    }

    @Test
    @DisplayName("카카오 엑세스 토큰 갱신 테스트[성공]")
    void refreshIfAccessTokenExpiredTest() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";
        Long userId = 1L;
        TokenInfoResponse tokenInfoResponse = new TokenInfoResponse(accessToken, "tokenType", refreshToken, 100L, 100L);
        kakaoTokenService.saveToken(userId, tokenInfoResponse);
        accessTokenRepository.deleteById(userId);
        given(userApiCaller.refreshAccessToken(eq(refreshToken)))
                .willReturn(new RefreshedTokenInfoResponse(newAccessToken, "tokenType", 100L));

        // when
        String actual = kakaoTokenService.refreshIfAccessTokenExpired(userId);
        String nowAccessToken = accessTokenRepository.findById(userId).get().getAccessToken();

        // then
        assertThat(actual).isEqualTo(newAccessToken);
        assertThat(nowAccessToken).isEqualTo(newAccessToken);
    }
}