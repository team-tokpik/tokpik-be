package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserException implements BaseException {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않음");

    private final HttpStatus status;
    private final String message;
}
