package supernova.whokie.answer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import supernova.whokie.answer.Answer;
import supernova.whokie.user.Users;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findAllByPicker(Pageable pageable, Users user);
}
