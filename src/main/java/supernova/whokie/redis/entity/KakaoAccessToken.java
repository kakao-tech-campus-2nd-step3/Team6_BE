package supernova.whokie.redis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("kakaoAccess")
public class KakaoAccessToken {

    @Id
    private Long memberId;

    private String accessToken;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;

    public KakaoAccessToken(Long memberId, String accessToken, Long expiresIn) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }
}