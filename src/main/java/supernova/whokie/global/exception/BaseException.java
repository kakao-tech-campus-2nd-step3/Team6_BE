package supernova.whokie.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{
    private final HttpStatus status;
    private final String title;

    public BaseException(String message, HttpStatus status, String title) {
        super(message);
        this.status = status;
        this.title = title;
    }
}
