package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LoginException implements BaseException {

    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인 과정중 에러 발생, 재로그인 필요"),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT");

    private final HttpStatus status;
    private final String message;
}
