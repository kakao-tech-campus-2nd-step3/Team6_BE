package supernova.whokie.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kakaopay")
public record KakaoPayProperties(
        String secretKey,
        String readyUrl,
        String approveUrl,
        String approveRedirectUrl,
        String failRedirectUrl,
        String cancelRedirectUrl
) {

}
