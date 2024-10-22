package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileTypeMismatchException extends CustomException {
    private static final String DEFAULT_TITLE = "File Type Mismatch";

    public FileTypeMismatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST, DEFAULT_TITLE);
    }
}

