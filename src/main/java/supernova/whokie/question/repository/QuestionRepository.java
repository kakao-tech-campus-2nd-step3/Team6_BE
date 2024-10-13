package supernova.whokie.question.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.question.Question;
import supernova.whokie.question.QuestionStatus;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q ORDER BY function('RAND')")
    List<Question> findRandomQuestions(Pageable pageable);

    List<Question> findAllByGroupId(Long groupId);

    @Query("SELECT q FROM Question q WHERE q.questionStatus = 'APPROVED' AND q.groupId = :groupId ORDER BY function('RAND')")
    List<Question> findRandomGroupQuestions(@Param("groupId") Long groupId, Pageable pageable);

    Optional<Question> findByIdAndGroupId(Long questionId, Long groupId);

    @Query("SELECT q FROM Question q WHERE q.questionStatus = :status AND q.groupId = :groupId")
    List<Question> findAllByStatus(@Param("groupId") Long groupId, @Param("status") QuestionStatus status);
}
