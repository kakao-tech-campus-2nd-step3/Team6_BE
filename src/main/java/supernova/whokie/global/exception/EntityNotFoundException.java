package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends CustomException {
    private static final String DEFAULT_TITLE = "Data Not Found";

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, DEFAULT_TITLE);
    }
}

