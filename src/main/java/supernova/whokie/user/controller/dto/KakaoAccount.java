package supernova.whokie.user.controller.dto;

public record KakaoAccount(
    Profile profile,
    String name,
    String email,
    String birthyear,
    String gender
) {

}
