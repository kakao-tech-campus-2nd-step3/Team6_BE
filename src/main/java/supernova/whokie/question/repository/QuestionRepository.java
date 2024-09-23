package supernova.whokie.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.question.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
