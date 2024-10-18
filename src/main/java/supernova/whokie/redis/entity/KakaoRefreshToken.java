package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("kakaoRefresh")
public class KakaoRefreshToken {
    @Id
    private Long userId;

    @NotNull
    private String refreshToken;

    @NotNull
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;

    public KakaoRefreshToken(Long userId, String refreshToken, Long expiresIn) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}