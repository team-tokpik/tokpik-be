package org.example.tokpik_be.exception;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.tokpik_be.common.ApiExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiExceptionResponse<String>> generalException(GeneralException e) {

        ApiExceptionResponse<String> response = new ApiExceptionResponse<>(e.getMessage());

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse<Map<String, String>>> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {

        Map<String, String> message = extractExceptionMessage(e);
        ApiExceptionResponse<Map<String, String>> response = new ApiExceptionResponse<>(message);

        return ResponseEntity.badRequest().body(response);
    }

    private Map<String, String> extractExceptionMessage(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        return fieldErrors.stream()
            .collect(Collectors.toMap(FieldError::getField,
                fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("")
            ));
    }
}
