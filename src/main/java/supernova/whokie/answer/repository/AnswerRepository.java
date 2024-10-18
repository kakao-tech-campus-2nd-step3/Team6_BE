package supernova.whokie.answer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.answer.Answer;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT p FROM Answer p WHERE p.picker = :user AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Answer> findAllByPickerAndCreatedAtBetween(Pageable pageable, @Param("user") Users user, @Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

}
