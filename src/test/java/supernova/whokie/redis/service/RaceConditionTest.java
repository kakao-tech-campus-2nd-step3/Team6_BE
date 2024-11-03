package supernova.whokie.redis.service;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@MockBean({S3Client.class, S3Template.class, S3Presigner.class})
@TestPropertySource(properties = {
    "jwt.secret=abcd"
})
public class RaceConditionTest {

    @Autowired
    private RedisVisitCountRepository redisVisitCountRepository;

    @Autowired
    private RedisVisitService redisVisitService;

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        redissonClient.getKeys().flushall();
    }

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
        executorService.shutdown();

        Thread.sleep(1000);

        // then
        RedisVisitCount actual = redisVisitCountRepository.findById(hostId).orElseThrow();

        assertAll(
            () -> assertThat(actual.getDailyVisited()).isEqualTo(oldDailyVisited + threadCount),
            () -> assertThat(actual.getTotalVisited()).isEqualTo(oldTotalVisited + threadCount)
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
}

