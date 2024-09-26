package supernova.whokie.profile_answer.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import supernova.whokie.profile_answer.ProfileAnswer;

@Repository
public interface ProfileAnswerRepository extends JpaRepository<ProfileAnswer, Long> {

    @Query("SELECT pa FROM ProfileAnswer pa JOIN FETCH pa.profileQuestion WHERE pa.profileQuestion.user.id = :userId")
    Page<ProfileAnswer> findAllByUserId(Long userId, Pageable pageable);
}
