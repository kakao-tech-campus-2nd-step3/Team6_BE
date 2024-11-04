package supernova.whokie.profilequestion.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.profilequestion.ProfileQuestion;

import java.util.Optional;

public interface ProfileQuestionRepository extends JpaRepository<ProfileQuestion, Long> {

    Page<ProfileQuestion> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT pq FROM ProfileQuestion pq JOIN FETCH pq.user WHERE pq.id = :profileQuestionId")
    Optional<ProfileQuestion> findByIdWithUser(Long profileQuestionId);
}
