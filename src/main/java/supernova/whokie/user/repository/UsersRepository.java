package supernova.whokie.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.user.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
