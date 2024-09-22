package supernova.whokie.user.infrastructure.apiCaller.dto;

public record KakaoAccount(
    Profile profile,
    String name,
    String email,
    String birthyear,
    String gender
) {

}
