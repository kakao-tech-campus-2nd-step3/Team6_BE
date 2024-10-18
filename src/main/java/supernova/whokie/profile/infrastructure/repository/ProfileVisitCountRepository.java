package supernova.whokie.profile.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.profile.ProfileVisitCount;

import java.util.Optional;

public interface ProfileVisitCountRepository extends JpaRepository<ProfileVisitCount, Long> {
    Optional<ProfileVisitCount> findByHostId(Long hostId);
}
