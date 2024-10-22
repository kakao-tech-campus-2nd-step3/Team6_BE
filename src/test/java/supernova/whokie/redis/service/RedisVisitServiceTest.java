package supernova.whokie.redis.service;

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
import supernova.whokie.redis.infrastructure.repository.RedisVisitCountRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

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