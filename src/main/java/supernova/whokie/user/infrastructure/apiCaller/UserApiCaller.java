package supernova.whokie.user.infrastructure.apiCaller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import supernova.whokie.global.property.KakaoProperties;
import supernova.whokie.user.infrastructure.apiCaller.dto.KakaoAccount;
import supernova.whokie.user.infrastructure.apiCaller.dto.TokenInfoResponse;
import supernova.whokie.user.infrastructure.apiCaller.dto.UserInfoResponse;

@Component
@RequiredArgsConstructor
public class UserApiCaller {

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
            TokenInfoResponse response = restClient.post()
                .uri(URI.create(tokenUrl))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(TokenInfoResponse.class)
                .getBody();

            return response;
        } catch (ResourceAccessException e) {
            return null; //Todo 수정 예정
        }
    }

    public LinkedMultiValueMap<String, String> createAccessBody(String code) {
        String redirectUrl = kakaoProperties.redirectUri();

        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.clientId());
        body.add("redirect_url", redirectUrl);
        body.add("code", code);
        return body;
    }

    public KakaoAccount extractUserInfo(String code) {
        String userInfoUrl = kakaoProperties.userInfoUrl();

        TokenInfoResponse tokenResponse = getAccessToken(code);
        String accessToken = tokenResponse.accessToken();

        UserInfoResponse response = restClient.get()
            .uri(URI.create(userInfoUrl))
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .toEntity(UserInfoResponse.class)
            .getBody();

        return response.kakaoAccount();
    }
}
