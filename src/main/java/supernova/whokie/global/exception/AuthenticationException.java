package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends CustomException {

    private static final String DEFAULT_TITLE = "Authentication Error";

    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, DEFAULT_TITLE);
    }

    public AuthenticationException(String message, HttpStatus status, String title) {
        super(message, status, title);
    }
}
