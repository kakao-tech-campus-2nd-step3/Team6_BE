package supernova.whokie.redis.entity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("kakaoAccess")
public class KakaoAccessToken {

    @Id
    private Long userId;

    @NotNull
    private String accessToken;

    @NotNull
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;

    public KakaoAccessToken(Long userId, String accessToken, Long expiresIn) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }
}