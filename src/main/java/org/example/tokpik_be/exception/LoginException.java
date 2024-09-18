package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LoginException implements BaseException {

    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인 과정중 에러 발생, 재로그인 필요"),
    JOIN_REQUIRED(HttpStatus.BAD_REQUEST, "미가입 사용자, 가입 과정 필요");

    private final HttpStatus status;
    private final String message;
}
