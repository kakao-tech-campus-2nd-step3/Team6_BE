package supernova.whokie.friend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import supernova.whokie.friend.service.dto.KakaoDto;

@Component
@AllArgsConstructor
public class KakaoApiCaller {
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    // 카카오 팀 설정 후 구현할 예정
    public KakaoDto.Profile getKakaoFriends(String accessToken) {
        try {
            return restClient.get()
                    .uri("https://kapi.kakao.com/v1/api/talk/friends")
                    .header("Authorization", "Bearer " + accessToken)
                    .exchange((req, res) -> {
                        return objectMapper.readValue(res.getBody(), KakaoDto.Profile.class);
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
