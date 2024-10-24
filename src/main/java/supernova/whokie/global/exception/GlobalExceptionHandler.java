package supernova.whokie.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");

        e.getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            problemDetail.setDetail(errorMessage);
        });
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFoundException(EntityNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> authenticationException(AuthenticationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> unexpectedException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("Unknown error");
        log.error("Internal Server Error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> forbiddenException(ForbiddenException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ProblemDetail> InvalidEntityException(InvalidEntityException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(FileTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> FileTypeMismatchException(FileTypeMismatchException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
