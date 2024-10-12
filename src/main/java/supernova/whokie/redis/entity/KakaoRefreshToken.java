package supernova.whokie.redis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("kakaoRefresh")
public class KakaoRefreshToken {
    @Id
    private Long memberId;

    private String refreshToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;

    public KakaoRefreshToken(Long memberId, String refreshToken, Long expiresIn) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}