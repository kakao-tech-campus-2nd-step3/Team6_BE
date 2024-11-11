package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidConditionException extends CustomException {

    private static final String DEFAULT_TITLE = "Invalid Condition";

    public InvalidConditionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, DEFAULT_TITLE);
    }
}
