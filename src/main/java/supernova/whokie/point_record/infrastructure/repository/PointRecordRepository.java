package supernova.whokie.point_record.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;

import java.time.LocalDateTime;


public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    @Query("SELECT p FROM PointRecord p WHERE p.userId= :userId AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<PointRecord> findByUserIdPaging(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT p FROM PointRecord p WHERE p.userId= :userId AND p.option= :option AND p.createdAt BETWEEN :startDate AND :endDate")
    Page<PointRecord> findByUserIdAndOptionPaging(Long userId, PointRecordOption option, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
