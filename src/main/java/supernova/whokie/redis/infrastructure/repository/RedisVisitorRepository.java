package supernova.whokie.redis.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.RedisVisitor;

public interface RedisVisitorRepository extends CrudRepository<RedisVisitor, String> {
}
