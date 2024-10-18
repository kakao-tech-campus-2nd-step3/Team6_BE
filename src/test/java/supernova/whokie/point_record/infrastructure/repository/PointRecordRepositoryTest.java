package supernova.whokie.point_record.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(JpaConfig.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointRecordRepositoryTest {
    @Autowired
    private PointRecordRepository pointRecordRepository;

    private long userId;
    private List<PointRecord> records;

    @BeforeEach
    void setUp() {
        userId = 1L;
        records = createPointRecords();
    }

    @Test
    @DisplayName("일정 시간 사이 데이터를 특정 userId로 Paging 조회")
    void findByUserIdPagingTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<PointRecord> actual = pointRecordRepository.findByUserIdPaging(
                userId,
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                pageable);

        // then
        List<PointRecord> list = actual.getContent();
        assertAll(
                () -> assertThat(list).hasSize(2),
                () -> assertThat(list.get(0).getUserId()).isEqualTo(userId),
                () -> assertThat(list.get(1).getUserId()).isEqualTo(userId)
        );
    }

    @Test
    @DisplayName("일정 시간 사이 데이터를 특정 userId와 option으로 Paging 조회")
    void findByUserIdAndOptionPagingTest() {
        // given
        PointRecord record = records.get(0);
        Pageable pageable = PageRequest.of(0, 10);

        //         when
        Page<PointRecord> actual = pointRecordRepository.findByUserIdAndOptionPaging(
                userId,
                record.getOption(),
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                pageable);

        // then
        List<PointRecord> list = actual.getContent();
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getId()).isEqualTo(record.getId());
        assertThat(list.getFirst().getUserId()).isEqualTo(userId);
        assertThat(list.getFirst().getOption()).isEqualTo(record.getOption());
    }

    private List<PointRecord> createPointRecords() {
        PointRecord record1 = PointRecord.builder().id(1L).userId(userId).option(PointRecordOption.USED).build();
        PointRecord record2 = PointRecord.builder().id(2L).userId(userId).option(PointRecordOption.CHARGED).build();
        PointRecord record3 = PointRecord.builder().id(3L).userId(2L).option(PointRecordOption.USED).build();
        return pointRecordRepository.saveAll(List.of(record1, record2, record3));
    }
}