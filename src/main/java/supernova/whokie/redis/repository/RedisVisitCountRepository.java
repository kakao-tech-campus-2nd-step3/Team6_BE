package supernova.whokie.redis.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.RedisVisitCount;

public interface RedisVisitCountRepository extends CrudRepository<RedisVisitCount, Long> {
}
