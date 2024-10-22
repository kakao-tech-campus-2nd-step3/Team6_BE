package supernova.whokie.user.infrastructure.apicaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.global.property.KakaoProperties;
import supernova.whokie.user.infrastructure.apicaller.dto.RefreshedTokenInfoResponse;
import supernova.whokie.user.infrastructure.apicaller.dto.TokenInfoResponse;
import supernova.whokie.user.infrastructure.apicaller.dto.UserInfoResponse;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UserApiCaller {

    private final ObjectMapper objectMapper;
    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;

    public String createCodeUrl() {
        String authUrl = kakaoProperties.authUrl();
        String redirectUrl = kakaoProperties.redirectUri();

        String url = UriComponentsBuilder.fromHttpUrl(authUrl)
            .queryParam("client_id", kakaoProperties.clientId())
            .queryParam("redirect_uri", redirectUrl)
            .queryParam("response_type", "code")
            .queryParam("scope", "profile_image,account_email,name,gender,birthyear,friends,talk_message")
            .toUriString();

        return url;
    }

    public TokenInfoResponse getAccessToken(String code) {
        String tokenUrl = kakaoProperties.tokenUrl();
        LinkedMultiValueMap<String, String> body = createAccessBody(code);

        try {
            return restClient.post()
                .uri(URI.create(tokenUrl))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                        return objectMapper.readValue(response.getBody(), TokenInfoResponse.class);
                    }
                    throw new AuthenticationException("유효하지 않은 인가코드입니다.");
                });
        } catch (ResourceAccessException e) {
            throw new AuthenticationException("네트워크 환경이 불안정합니다.");
        }
    }

    public RefreshedTokenInfoResponse refreshAccessToken(String refreshToken) {
        LinkedMultiValueMap<String, String> body = createRefreshBody(refreshToken);

        try {
            return restClient.post()
                    .uri(URI.create(kakaoProperties.tokenUrl()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                            return objectMapper.readValue(response.getBody(), RefreshedTokenInfoResponse.class);
                        }
                        throw new AuthenticationException("토큰 갱신에 실패하였습니다.");
                    });
        } catch (ResourceAccessException e) {
            throw new AuthenticationException("네트워크 환경이 불안정합니다.");
        }
    }

    public @NotNull LinkedMultiValueMap<String, String> createAccessBody(String code) {
        String redirectUrl = kakaoProperties.redirectUri();

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_url", redirectUrl);
        body.add("code", code);
        return body;
    }

    private @NotNull LinkedMultiValueMap<String, String> createRefreshBody(String refreshToken) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoProperties.clientId());
        body.add("refresh_token", refreshToken);
        return body;
    }

    public UserInfoResponse extractUserInfo(String accessToken) {
        String userInfoUrl = kakaoProperties.userInfoUrl();

        return restClient.get()
            .uri(URI.create(userInfoUrl))
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .toEntity(UserInfoResponse.class)
            .getBody();
    }
}
