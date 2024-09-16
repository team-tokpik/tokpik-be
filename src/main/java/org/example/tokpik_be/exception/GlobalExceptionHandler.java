package org.example.tokpik_be.exception;

import org.example.tokpik_be.common.ApiExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiExceptionResponse> generalException(GeneralException e) {

        ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage());

        return ResponseEntity.status(e.getStatus()).body(response);
    }
}
