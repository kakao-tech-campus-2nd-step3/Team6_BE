package supernova.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfig {
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_HOST = "localhost";
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServerBuilder()
                .port(REDIS_PORT)
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
        try {
            redisServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void stopRedisServer() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT);
        return Redisson.create(config);
    }
}
