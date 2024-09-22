package supernova.whokie.user.infrastructure.apiCaller.dto;

public record TokenInfoResponse(
    String access_token,
    String token_type,
    String refresh_token,
    Long expires_in,
    Long refresh_token_expires_in
) {

}
