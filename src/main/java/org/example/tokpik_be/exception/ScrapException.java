package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapException implements BaseException {

    SCRAP_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 스크랩"),
    SCRAP_TOPIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 스크랩 대화 주제"),
    INVALID_SCRAP_TOPIC(HttpStatus.BAD_REQUEST, "해당 스크랩에 저장되지 않은 스크랩 대화 주제"),
    UNAUTHORIZED_SCRAP_ACCESS(HttpStatus.UNAUTHORIZED, "자신의 스크랩만 접근 가능");

    private final HttpStatus status;
    private final String message;
}
