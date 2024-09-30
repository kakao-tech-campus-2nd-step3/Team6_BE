package supernova.whokie.global.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings
                .DEFAULTS
                .withReadTimeout(Duration.ofSeconds(2))
                .withConnectTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }
}
