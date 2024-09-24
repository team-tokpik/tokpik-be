package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagException implements BaseException {

    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 태그");

    private final HttpStatus status;
    private final String message;

}
