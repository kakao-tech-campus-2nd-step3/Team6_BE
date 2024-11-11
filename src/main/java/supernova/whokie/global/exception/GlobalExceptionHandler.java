package supernova.whokie.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        Map<String, Object> errors = new HashMap<>();
        e.getAllErrors()
            .forEach(
                field -> errors.put(((FieldError) field).getField(), field.getDefaultMessage()));

        problemDetail.setTitle("Validation Error");
        problemDetail.setProperties(errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(
        ConstraintViolationException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        Map<String, Object> errors = new HashMap<>();
        e.getConstraintViolations()
            .forEach(
                violation -> errors.put(violation.getPropertyPath().toString(),
                    violation.getMessage()));

        problemDetail.setTitle("Validation Error");
        problemDetail.setProperties(errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFoundException(EntityNotFoundException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> authenticationException(AuthenticationException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
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
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ProblemDetail> invalidEntityException(InvalidEntityException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(FileTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> fileTypeMismatchException(FileTypeMismatchException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(InviteCodeException.class)
    public ResponseEntity<ProblemDetail> inviteCodeException(InviteCodeException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(InvalidConditionException.class)
    public ResponseEntity<ProblemDetail> invalidConditionException(InvalidConditionException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    private ProblemDetail setCustomProblemDetail(CustomException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }
}
