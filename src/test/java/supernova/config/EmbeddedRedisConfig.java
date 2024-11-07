package supernova.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_HOST = "localhost";
    private RedisServer redisServer;
    private RedissonClient redissonClient;

    @PostConstruct
    public void startRedis() throws IOException, InterruptedException {
        // Redis 서버를 시작합니다.
        redisServer = new RedisServer(REDIS_PORT);
        redisServer.start();

        Thread.sleep(1000); // 1초 대기
        // RedissonClient를 설정하여 Redis에 연결합니다.
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT);
        redissonClient = org.redisson.Redisson.create(config);
        System.out.println("Redisson 클라이언트가 " + REDIS_HOST + ":" + "REDIS_PORT에 연결되었습니다.");
    }

    @PreDestroy
    public void stopRedisServer() {
        if (redisServer != null) {
            redisServer.stop();
        }
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }

    @Bean
    public RedissonClient redissonClient() {
        return redissonClient;
    }
}
