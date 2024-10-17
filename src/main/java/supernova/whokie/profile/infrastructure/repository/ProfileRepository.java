package supernova.whokie.profile.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.profile.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUsersId(Long userId);
}
