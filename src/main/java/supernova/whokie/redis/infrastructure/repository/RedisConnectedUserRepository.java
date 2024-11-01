package supernova.whokie.redis.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.ConnectedUser;

public interface RedisConnectedUserRepository extends CrudRepository<ConnectedUser, Long> {
}
