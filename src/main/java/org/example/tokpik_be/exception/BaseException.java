package org.example.tokpik_be.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {
    HttpStatus getStatus();
    String getMessage();
}
