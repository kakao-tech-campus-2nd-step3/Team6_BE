package supernova.whokie.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import supernova.whokie.global.property.RedisProperties;

@Profile("default")
@Configuration
@RequiredArgsConstructor
public class DefaultRedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Key는 String으로 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value는 기본적으로 Jdk 직렬화로 저장
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
