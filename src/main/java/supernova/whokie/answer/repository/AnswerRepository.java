package supernova.whokie.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supernova.whokie.answer.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
