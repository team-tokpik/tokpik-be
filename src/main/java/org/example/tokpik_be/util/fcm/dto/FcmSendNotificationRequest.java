package org.example.tokpik_be.util.fcm.dto;

import org.example.tokpik_be.notification.event.NotificationSendEvent;

public record FcmSendNotificationRequest(
    String receiverToken,
    String title,
    String content
) {

    public static FcmSendNotificationRequest from(NotificationSendEvent sendEvent) {

        return new FcmSendNotificationRequest(sendEvent.receiverToken(),
            sendEvent.title(),
            sendEvent.content());
    }
}
