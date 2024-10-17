package supernova.whokie.redis.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.VisitCount;

public interface VisitCountRepository extends CrudRepository<VisitCount, Long> {
}
