package supernova.whokie.profile_answer.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile_answer.ProfileAnswer;

import java.util.Optional;

@Repository
public interface ProfileAnswerRepository extends JpaRepository<ProfileAnswer, Long> {

    @Query("SELECT pa FROM ProfileAnswer pa JOIN FETCH pa.profileQuestion WHERE pa.profileQuestion.user.id = :userId")
    Page<ProfileAnswer> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT pa FROM ProfileAnswer pa JOIN FETCH pa.answeredUser WHERE pa.id = :id")
    Optional<ProfileAnswer> findByIdWithAnsweredUser(Long id);

    @Transactional
    void deleteAllByProfileQuestionId(Long profileQuestionId);
}
