package org.example.tokpik_be.notification.dto.response;

public record NotificationScheduledResponse(
    String receiverToken,
    String talkTopicTitle,
    String talkTopicSubtitle
) {

}
