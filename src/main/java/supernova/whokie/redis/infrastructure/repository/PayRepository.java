package supernova.whokie.redis.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.PayToken;

public interface PayRepository extends CrudRepository<PayToken, Long> {
}
