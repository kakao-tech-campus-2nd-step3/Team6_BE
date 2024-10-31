package supernova.whokie.user.infrastructure.apicaller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccount(
        Profile profile,
        String name,
        String email,

        @JsonProperty("birthyear")
        String birthYear,
        String gender
) {

}
