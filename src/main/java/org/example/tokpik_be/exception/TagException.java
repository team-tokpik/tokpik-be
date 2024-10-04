package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagException implements BaseException {

    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 태그"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "1개 이상의 태그 선택 필요"),
    DUPLICATE_TAGS(HttpStatus.BAD_REQUEST, "중복된 태그 요청 존재");

    private final HttpStatus status;
    private final String message;

}
