package supernova.whokie.point_record.infrastructure.apicaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import supernova.whokie.global.exception.AuthenticationException;
import supernova.whokie.global.property.KakaoPayProperties;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayReadyInfoResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PayApiCaller {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final KakaoPayProperties kakaoPayProperties;;

    public PayReadyInfoResponse payReady(int point) {
        String url = kakaoPayProperties.readyUrl();
        Map<String, String> body = createPayReadyBody(point);
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            return restClient.post()
                    .uri(URI.create(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "SECRET_KEY "+ kakaoPayProperties.secretKey())
                    .body(jsonBody)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                            return objectMapper.readValue(response.getBody(), PayReadyInfoResponse.class);
                        }
                        throw new AuthenticationException("유효하지 않은 시크릿코드입니다.");
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }

    public PayApproveInfoResponse payApprove(String tid, String pgToken) {
        String url = kakaoPayProperties.approveUrl();
        Map<String, String> body = createPayApproveBody(tid, pgToken);
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            return restClient.post()
                    .uri(URI.create(url))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "SECRET_KEY "+ kakaoPayProperties.secretKey())
                    .body(jsonBody)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                            return objectMapper.readValue(response.getBody(), PayApproveInfoResponse.class);
                        }
                        throw new AuthenticationException("유효하지 않은 시크릿코드입니다.");
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
    }


    public @NotNull HashMap<String, String> createPayReadyBody(int point) {
        var body = new HashMap<String, String>();
        body.put("cid", "TC0ONETIME");
        body.put("partner_order_id", "partner_order_id");
        body.put("partner_user_id", "partner_user_id");
        body.put("item_name", "포인트");
        body.put("quantity", String.valueOf(point));
        body.put("total_amount", String.valueOf(point));
        body.put("tax_free_amount", "0");
        body.put("approval_url", "http://localhost:8080/api/point/purchase/approve");
        body.put("fail_url", "http://localhost:8080");
        body.put("cancel_url", "http://localhost:8080");

        return body;
    }

    public @NotNull HashMap<String, String> createPayApproveBody(String tid, String pgToken) {
        var body = new HashMap<String, String>();
        body.put("cid", "TC0ONETIME");
        body.put("tid", tid);
        body.put("partner_order_id", "partner_order_id");
        body.put("partner_user_id", "partner_user_id");
        body.put("pg_token", pgToken);

        return body;
    }



}
