package supernova.whokie.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("redis")
public record RedisProperties(
        String host,
        int port
) {

}
