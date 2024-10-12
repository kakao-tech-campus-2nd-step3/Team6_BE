package supernova.whokie.redis.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.KakaoAccessToken;

public interface AccessTokenRepository extends CrudRepository<KakaoAccessToken, Long> {
}