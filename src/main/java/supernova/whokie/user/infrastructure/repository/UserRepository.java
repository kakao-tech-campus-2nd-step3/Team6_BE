package supernova.whokie.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.user.Users;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    List<Users> findByKakaoIdIn(List<Long> kakaoId);

    List<Users> findByIdIn(List<Long> ids);
}
