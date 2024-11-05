package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;

public class InviteCodeException extends CustomException {

    private static final String DEFAULT_TITLE = "Invite Code Error";

    public InviteCodeException(String message) {
        super(message, HttpStatus.FORBIDDEN, DEFAULT_TITLE);
    }

}
