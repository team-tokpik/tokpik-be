package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TalkTopicException implements BaseException {

    TALK_TOPIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 대화 주제");

    private final HttpStatus status;
    private final String message;
}
