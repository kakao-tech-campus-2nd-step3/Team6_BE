package supernova.whokie.redis.service;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import supernova.config.EmbeddedRedisConfig;
import supernova.whokie.group.Groups;
import supernova.whokie.group.infrastructure.repository.GroupRepository;
import supernova.whokie.ranking.Ranking;
import supernova.whokie.ranking.infrastructure.repoistory.RankingRepository;
import supernova.whokie.ranking.service.RankingWriterService;
import supernova.whokie.redis.entity.RedisVisitCount;
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.infrastructure.repository.UserRepository;

import java.time.LocalDate;
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
@Import(EmbeddedRedisConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RaceConditionTest {

    @Autowired
    private RedisVisitCountRepository redisVisitCountRepository;

    @Autowired
    private RedisVisitService redisVisitService;

    @Autowired
    private RankingWriterService rankingWriterService;

    @Autowired
    RankingRepository rankingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RedissonClient redissonClient;

    Users user;
    Groups group;

    @BeforeEach
    void setUp() {
        redissonClient.getKeys().flushall();
        user = createUser();
        group = createGroup();
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

        // then
        RedisVisitCount actual = redisVisitCountRepository.findById(hostId).orElseThrow();

        assertAll(
            () -> assertThat(actual.getDailyVisited()).isEqualTo(oldDailyVisited + threadCount),
            () -> assertThat(actual.getTotalVisited()).isEqualTo(oldTotalVisited + threadCount)
        );
    }

    @Test
    @DisplayName("동시 질문 지목 횟수 증가 테스트")
    void AnswerCountConcurrentlyTest() throws InterruptedException {
        // given
        createRanking(user, group);
        int threadCount = 100; // 스레드 개수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    rankingWriterService.increaseRankingCountByUserAndQuestionAndGroups(user, "test", group);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        Ranking actual = rankingRepository.findByUsersAndQuestionAndGroups(user, "test", group)
            .orElseThrow();

        assertAll(
            () -> assertThat(actual.getCount()).isEqualTo(threadCount)
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

    private Users createUser() {
        Users user = Users.builder()
            .name("test")
            .email("test@gmail.com")
            .point(1000)
            .birthDate(LocalDate.now())
            .kakaoId(1L)
            .gender(Gender.M)
            .role(Role.USER)
            .build();

        userRepository.save(user);
        return user;
    }

    private Groups createGroup() {
        Groups group = Groups.builder()
            .groupName("test")
            .description("test")
            .groupImageUrl("test")
            .build();

        groupRepository.save(group);
        return group;
    }

    private void createRanking(Users user, Groups group) {
        Ranking ranking = Ranking.builder()
            .question("test")
            .count(0)
            .users(user)
            .groups(group)
            .build();

        rankingRepository.save(ranking);
    }
}

