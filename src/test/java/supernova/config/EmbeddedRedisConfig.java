package supernova.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;

@Profile({"dev", "default"})
@TestConfiguration
public class EmbeddedRedisConfig {
    private static final int REDIS_PORT = 6379;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServerBuilder()
                .port(REDIS_PORT)
                .setting("maxheap 16mb")
                .build();
        redisServer.start();
    }

    @PreDestroy
    public void stopRedisServer() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
