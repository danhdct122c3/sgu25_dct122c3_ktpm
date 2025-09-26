package fpl.sd.backend.exception;

import fpl.sd.backend.dto.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<APIResponse<Object>> handleRuntimeException(RuntimeException e) {
        APIResponse<Object> apiResponse = APIResponse.builder()
                .message(e.getMessage())
                .flag(false)
                .code(400)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);

    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        APIResponse<Object> apiResponse = APIResponse.builder()
                .message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
                .flag(false)
                .code(400)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
