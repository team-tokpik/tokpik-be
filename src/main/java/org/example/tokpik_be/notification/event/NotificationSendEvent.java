package org.example.tokpik_be.notification.event;

public record NotificationSendEvent(
    String receiverToken,
    String title,
    String content
) {

}
