package supernova.whokie.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
public class RequireAdditionalDataException extends CustomException {
    private static final String DEFAULT_TITLE = "Additional data required to fulfill preconditions";

    public RequireAdditionalDataException(String message) {
        super(message, HttpStatus.PRECONDITION_REQUIRED, DEFAULT_TITLE);
    }
}
