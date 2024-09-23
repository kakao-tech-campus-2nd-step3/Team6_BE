package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends BaseException {
    private static final String DEFAULT_TITLE = "데이터를 찾을 수 없습니다.";

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, DEFAULT_TITLE);
    }

    public EntityNotFoundException(String message, HttpStatus status, String title) {
        super(message, status, title);
    }
}