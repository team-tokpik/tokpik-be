package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapException implements BaseException {

    SCRAP_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 스크랩");

    private final HttpStatus status;
    private final String message;
}
