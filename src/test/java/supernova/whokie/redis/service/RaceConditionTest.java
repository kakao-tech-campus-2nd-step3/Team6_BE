package supernova.whokie.redis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.profile.service.ProfileVisitReadService;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.infrastructure.repository.AccessTokenRepository;
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;
import supernova.whokie.redis.infrastructure.repository.RedisVisitorRepository;

@DataRedisTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisVisitService.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({EmbeddedRedisConfig.class, ProfileVisitReadService.class})
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
public class RaceConditionTest {

    @Autowired
    private RedisVisitCountRepository redisVisitCountRepository;

    @MockBean
    private ProfileVisitCountRepository profileVisitCountRepository;

    @Autowired
    private RedisVisitService redisVisitService;

    @Test
    @DisplayName("동시 방문자 수 증가 테스트")
    void visitProfileConcurrentlyTest() throws InterruptedException {
        // given
        RedisVisitCount redisVisitCount = createVisitCount();
        Long hostId = redisVisitCount.getHostId();
        String visitorIp = "visitorIp";
        int oldDailyVisited = redisVisitCount.getDailyVisited();
        int oldTotalVisited = redisVisitCount.getTotalVisited();

        int threadCount = 100; // 스레드 개수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    redisVisitService.visitProfile(hostId, visitorIp + finalI);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        RedisVisitCount actual = redisVisitCountRepository.findById(hostId).orElseThrow();
        assertAll(
            () -> assertThat(actual.getDailyVisited()).isEqualTo(oldDailyVisited + threadCount),
            () -> assertThat(actual.getTotalVisited()).isEqualTo(oldTotalVisited + threadCount)
        );

        executorService.shutdown();
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
}

