package supernova.whokie.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.user.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
}
