package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistException extends CustomException {

    private static final String DEFAULT_TITLE = "Already Exist";

    public AlreadyExistException(String message) {
        super(message, HttpStatus.BAD_REQUEST, DEFAULT_TITLE);
    }

}
