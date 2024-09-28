package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException {

    private static final String DEFAULT_TITLE = "Forbidden";

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, DEFAULT_TITLE);
    }
}
