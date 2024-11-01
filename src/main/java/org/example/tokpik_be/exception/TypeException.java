package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TypeException implements BaseException {

    TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 타입"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "1개 이상의 타입 선택 필요"),
    DUPLICATE_TYPES(HttpStatus.BAD_REQUEST, "중복된 타입의 요청 존재");

    private final HttpStatus status;
    private final String message;

}
