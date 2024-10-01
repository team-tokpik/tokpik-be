package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationException implements BaseException {

    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 알림"),
    INVALID_NOTIFICATION_INTERVAL(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 간격"),
    UNAUTHORIZED_NOTIFICATION_DELETION(HttpStatus.BAD_REQUEST, "자신의 알림만 삭제 가능"),
    CAN_NOTICE_TALK_TOPICS_IN_SCRAP(HttpStatus.BAD_REQUEST, "스크랩 내에 포함되는 주제만 알림 가능");

    private final HttpStatus status;
    private final String message;
}
