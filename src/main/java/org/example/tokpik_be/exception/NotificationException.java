package org.example.tokpik_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationException implements BaseException {

    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 알림"),
    INVALID_NOTIFICATION_INTERVAL(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 간격");

    private final HttpStatus status;
    private final String message;
}
