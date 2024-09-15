package supernova.whokie.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.user.Users;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findByKakaoCodeIn(List<String> kakaoCode);

}
