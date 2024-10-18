package supernova.whokie.profile.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.profile.ProfileVisitor;

import java.time.LocalDateTime;

public interface ProfileVisitorRepository extends JpaRepository<ProfileVisitor, Long> {

    boolean existsByHostIdAndVisitorIpAndModifiedAtBetween(
            Long hostId, String visitorIp, LocalDateTime start, LocalDateTime end
    );
}
