package supernova.whokie.redis.repository;

import org.springframework.data.repository.CrudRepository;
import supernova.whokie.redis.entity.Visitor;

public interface VisitorRepository extends CrudRepository<Visitor, String> {
}
