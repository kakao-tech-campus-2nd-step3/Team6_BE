package supernova.whokie.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.question.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
