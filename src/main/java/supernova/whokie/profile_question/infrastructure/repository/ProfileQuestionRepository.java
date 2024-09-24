package supernova.whokie.profile_question.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supernova.whokie.profile_question.ProfileQuestion;

@Repository
public interface ProfileQuestionRepository extends JpaRepository<ProfileQuestion, Long> {

    Page<ProfileQuestion> findAllByUserId(Long userId, Pageable pageable);
}
