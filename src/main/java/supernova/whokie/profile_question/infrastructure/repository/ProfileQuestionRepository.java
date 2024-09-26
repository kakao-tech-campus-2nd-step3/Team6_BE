package supernova.whokie.profile_question.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import supernova.whokie.profile_question.ProfileQuestion;

@Repository
public interface ProfileQuestionRepository extends JpaRepository<ProfileQuestion, Long> {

    Page<ProfileQuestion> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT pq FROM ProfileQuestion pq JOIN FETCH pq.user WHERE pq.id = :profileQuestionId")
    Optional<ProfileQuestion> findByIdWithUser(Long profileQuestionId);
}
