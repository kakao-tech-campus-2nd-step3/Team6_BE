package supernova.whokie.answer.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import supernova.whokie.answer.Answer;
import supernova.whokie.user.Users;

import java.time.LocalDateTime;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @EntityGraph(attributePaths = {"picked"})
    @Query("SELECT p FROM Answer p WHERE p.picked = :user AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<Answer> findAllByPickedAndCreatedAtBetween(Pageable pageable, @Param("user") Users user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
