package supernova.whokie.redis.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.KakaoRefreshToken;

public interface RefreshTokenRepository extends CrudRepository<KakaoRefreshToken, Long> {
}