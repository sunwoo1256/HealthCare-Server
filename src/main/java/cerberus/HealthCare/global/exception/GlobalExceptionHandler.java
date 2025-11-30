package cerberus.HealthCare.global.exception;

import cerberus.HealthCare.global.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CoreException 처리
     */
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<BaseResponse<?>> handleCoreException(CoreException e) {
        ErrorCode code = e.getErrorCode();
        log.error("[CoreException] {}: {}", code.getCode(), code.getMessage(), e);

        return BaseResponse.of(code.getStatus(), code.getMessage());
    }

    /**
     * @Valid 검증 실패 시
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
            .getAllErrors()
            .stream()
            .map(err -> {
                if (err instanceof FieldError fieldError) {
                    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                } else {
                    return err.getDefaultMessage();
                }
            })
            .collect(Collectors.joining(", "));

        log.warn("[ValidationException] {}", errorMessage);

        return BaseResponse.of(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * 그 외 알 수 없는 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("[Exception] {}", e.getMessage(), e);

        return BaseResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}

