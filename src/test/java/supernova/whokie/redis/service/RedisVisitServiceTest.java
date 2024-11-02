package supernova.whokie.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.profile.service.ProfileVisitReadService;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.entity.RedisVisitor;
import supernova.whokie.redis.infrastructure.repository.RedisVisitorRepository;
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@DataRedisTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisVisitService.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(EmbeddedRedisConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=abcd"
})
class RedisVisitServiceTest {
    @Autowired
    private RedisVisitService redisVisitService;
    @Autowired
    private RedisVisitCountRepository redisVisitCountRepository;
    @MockBean
    private ProfileVisitReadService profileVisitReadService;

    RedisVisitCount redisVisitCount;
    List<RedisVisitor> redisVisitors;
    @Autowired
    private RedisVisitorRepository redisVisitorRepository;

    @BeforeEach
    void setUp() {
        redisVisitCount = createVisitCount();
        redisVisitors = createRedisVisitors();
    }

    @Test
    @DisplayName("방문자 수 증가 테스트")
    void visitProfileTest() {
        // given
        RedisVisitCount redisVisitCount = createVisitCount();
        Long hostId = redisVisitCount.getHostId();
        String visitorIp = "visitorIp";
        int oldDailyVisited = redisVisitCount.getDailyVisited();
        int oldTotalVisited = redisVisitCount.getTotalVisited();

        // when
        RedisVisitCount actual = redisVisitService.visitProfile(hostId, visitorIp);

        // then
        assertAll(
                () -> assertThat(actual.getDailyVisited()).isEqualTo(oldDailyVisited + 1),
                () -> assertThat(actual.getTotalVisited()).isEqualTo(oldTotalVisited + 1),
                () -> assertThat(redisVisitService.checkVisited(hostId, visitorIp)).isTrue()
        );
    }

    @Test
    @DisplayName("VisitCount에 key가 없을 경우 테스트")
    void findVisitCountByHostIdTest() {
        // given
        Long hostId = 12315213L;
        int dailyVisited = 100;
        int totalVisited = 1000;
        RedisCommand.Visited command = RedisCommand.Visited.builder().hostId(hostId).dailyVisited(dailyVisited).totalVisited(totalVisited).build();
        given(profileVisitReadService.findVisitCountById(eq(hostId)))
                .willReturn(command);

        // when
        RedisVisitCount actual = redisVisitService.findVisitCountByHostId(hostId);

        // then
        assertAll(
                () -> assertThat(actual.getDailyVisited()).isEqualTo(dailyVisited),
                () -> assertThat(actual.getTotalVisited()).isEqualTo(totalVisited)
        );
    }

    @Test
    @DisplayName("Visitor 전체 조회 테스트")
    void findAllVisitorTest() {
        // given
        List<RedisVisitor> visitorList = redisVisitors;

        // when
        List<RedisVisitor> actuals = redisVisitService.findAllVisitor();

        // then
        assertThat(actuals).hasSize(visitorList.size());
    }

    @Test
    @DisplayName("Visitor 리스트 전체 삭제 테스트")
    void deleteAllVisitorTest() {
        // given
        List<RedisVisitor> visitorList = redisVisitors;
        RedisVisitor remainEntity = redisVisitors.get(0);
        visitorList.remove(remainEntity);

        // when
        redisVisitService.deleteAllVisitor();
        List<RedisVisitor> actuals = redisVisitService.findAllVisitor();

        // then
        assertAll(
                () -> assertThat(actuals).hasSize(0)
        );
    }


    private RedisVisitCount createVisitCount() {
        RedisVisitCount redisVisitCount = RedisVisitCount.builder()
                .hostId(1L)
                .dailyVisited(0)
                .totalVisited(10)
                .build();
        redisVisitCountRepository.save(redisVisitCount);
        return redisVisitCount;
    }

    private List<RedisVisitor> createRedisVisitors() {
        List<RedisVisitor> redisVisitors = new ArrayList<>();
        RedisVisitor visitor1 = RedisVisitor.builder().hostId(1L).build();
        RedisVisitor visitor2 = RedisVisitor.builder().hostId(2L).build();
        RedisVisitor visitor3 = RedisVisitor.builder().hostId(3L).build();
        redisVisitorRepository.saveAll(List.of(visitor1, visitor2, visitor3)).forEach(redisVisitors::add);
        return redisVisitors;
    }

}