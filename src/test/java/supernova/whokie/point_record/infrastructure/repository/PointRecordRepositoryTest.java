package supernova.whokie.point_record.infrastructure.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import supernova.whokie.global.config.JpaConfig;
import supernova.whokie.point_record.PointRecord;
import supernova.whokie.point_record.PointRecordOption;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointRecordRepositoryTest {
    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Test
    @DisplayName("일정 시간 사이 데이터를 특정 userId로 Paging 조회")
    void findByUserIdPagingTest() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PointRecord record1 = PointRecord.builder().id(1L).userId(userId).build();
        PointRecord record2 = PointRecord.builder().id(2L).userId(userId).build();
        PointRecord record3 = PointRecord.builder().id(3L).userId(2L).build();
        pointRecordRepository.save(record1);
        pointRecordRepository.save(record2);
        pointRecordRepository.save(record3);
//         when
        Page<PointRecord> actual = pointRecordRepository.findByUserIdPaging(
                userId,
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                pageable);

        // then
        List<PointRecord> list = actual.getContent();
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getUserId()).isEqualTo(userId);
        assertThat(list.get(1).getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("일정 시간 사이 데이터를 특정 userId와 option으로 Paging 조회")
    void findByUserIdAndOptionPagingTest() {
        // given
        Long userId = 1L;
        PointRecordOption option = PointRecordOption.USED;
        Pageable pageable = PageRequest.of(0, 10);
        PointRecord record1 = PointRecord.builder().id(1L).userId(userId).option(option).build();
        PointRecord record2 = PointRecord.builder().id(2L).userId(userId).option(PointRecordOption.CHARGED).build();
        PointRecord record3 = PointRecord.builder().id(3L).userId(2L).option(option).build();
        pointRecordRepository.save(record1);
        pointRecordRepository.save(record2);
        pointRecordRepository.save(record3);
//         when
        Page<PointRecord> actual = pointRecordRepository.findByUserIdAndOptionPaging(
                userId,
                option,
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                pageable);

        // then
        List<PointRecord> list = actual.getContent();
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getId()).isEqualTo(record1.getId());
        assertThat(list.getFirst().getUserId()).isEqualTo(userId);
        assertThat(list.getFirst().getOption()).isEqualTo(option);
    }
}