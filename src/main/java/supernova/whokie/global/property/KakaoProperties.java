package supernova.whokie.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record KakaoProperties(
    String redirectUri,
    String clientId,
    String authUrl,
    String tokenUrl,
    String userInfoUrl
) {

}
