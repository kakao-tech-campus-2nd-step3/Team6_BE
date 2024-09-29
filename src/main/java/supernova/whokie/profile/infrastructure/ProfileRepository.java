package supernova.whokie.profile.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.profile.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUsersId(Long userId);
}
